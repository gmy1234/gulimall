package com.gmy.guliorder.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.gmy.common.constant.OrderConstant;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.common.utils.R;
import com.gmy.common.vo.MemberResponseVo;
import com.gmy.guliorder.order.dao.OrderDao;
import com.gmy.guliorder.order.dao.OrderItemDao;
import com.gmy.guliorder.order.dto.SpuInfoDTO;
import com.gmy.guliorder.order.entity.OrderEntity;
import com.gmy.guliorder.order.entity.OrderItemEntity;
import com.gmy.guliorder.order.feign.CartFeignService;
import com.gmy.guliorder.order.feign.MemberFeignService;
import com.gmy.guliorder.order.feign.ProductFeignService;
import com.gmy.guliorder.order.feign.WareFeignService;
import com.gmy.guliorder.order.interceptor.LoginUserInterceptor;
import com.gmy.guliorder.order.service.OrderItemService;
import com.gmy.guliorder.order.service.OrderService;
import com.gmy.guliorder.order.vo.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVO> threadLocal = new ThreadLocal<>();

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();

        // 解决异步调用请求头丢失问题
        // 从主线程那数据，副线程共享
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAllAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 1. 远程查询 所有的收获列表
            List<OrderConfirmVo.MemberAddressVO> userAddress = memberFeignService.getAddressById(memberResponseVo.getId());
            confirmVo.setAddress(userAddress);
        }, executor);

        CompletableFuture<Void> checkItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 2. 远程查询购物车所有选中的购物项
            List<OrderConfirmVo.OrderItemVO> cartItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(cartItems);
        }, executor).thenRunAsync( ()->{
            List<OrderConfirmVo.OrderItemVO> items = confirmVo.getItems();
            List<Long> skuIds = items.stream()
                    .map(OrderConfirmVo.OrderItemVO::getSkuId)
                    .collect(Collectors.toList());
            // 查询商品ID对应的库存状态
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            List<SkuHasStockVo> data = skuHasStock.getData(new TypeReference<List<SkuHasStockVo>>() {});
            if (data != null) {
                // 库存数据 转换成 map
                Map<Long, Boolean> productStatus = data.stream()
                        .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getStock));
                confirmVo.setStocks(productStatus);
            }

        });


        // 3.用户的积分
        Integer integration = memberResponseVo.getIntegration();
        confirmVo.setIntegration(integration);

        // 4. 其他计算

        // TODO:5.订单防止重复令牌
        String uuid = UUID.randomUUID().toString().replace("-", "");
        confirmVo.setOrderToken(uuid);
        redisTemplate.opsForValue().set(OrderConstant.USER_TOKEN_PREFIX + memberResponseVo.getId(), uuid, 30, TimeUnit.MINUTES );
        // 等待所有的异步任务完成。
        CompletableFuture.allOf(getAllAddressFuture, checkItemFuture).get();
        return confirmVo;
    }

    @Override
    @Transactional
    public OrderSubmitResponseVO submitOrder(OrderSubmitVO vo) {
        MemberResponseVo member = LoginUserInterceptor.loginUser.get();
        OrderSubmitResponseVO res = new OrderSubmitResponseVO();
        res.setCode(0);
        //下单流程；创建订单，验证令牌，验证价格，锁库存
        // 1.验证令牌 [令牌的对比和删除必须保证原子性]
        // 订单的令牌
        String orderToken = vo.getOrderToken();
        // 这段脚本的意思是，如果获取key对应的value是传过来的值，那就调用删除方法返回1，否则返回0
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // 原子删锁
        /*
            实现RedisScript接口的实现类(脚本内容,执行完之后的返回值类型)
            一个存key的list
            要对比的value
         */
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(OrderConstant.USER_TOKEN_PREFIX + member.getId(), orderToken));

        // 校验令牌：
        if (result == 0L) {
            // 验证失败
            res.setCode(1);
        }else {
            // 创建订单项
            OrderCreateTo order = this.createOrder();

            // 3.比较价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                // 价格无误：
                // 4、将准备好的订单信息保存到数据库
                this.saveOrderAndItems(order);

                // 5、库存锁定 是个事务（没有库存，以上的操作需要回滚）
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                // 封装 数据，得到锁定库存的SKuID 和 该商品的数量
                List<OrderConfirmVo.OrderItemVO> locks = order.getOrderItems().stream().map(it -> {
                    OrderConfirmVo.OrderItemVO itemVO = new OrderConfirmVo.OrderItemVO();
                    itemVO.setSkuId(it.getSkuId());
                    itemVO.setCount(it.getSkuQuantity());
                    return itemVO;
                }).collect(Collectors.toList());
                // 设置锁库存
                lockVo.setLocks(locks);
                // TODO: 远程锁库存，
                // 库存成功， 网络原因，业务失败，订单回滚，库存不回滚，之前锁定的库存就要解锁。我们要自动解锁。
                // 远程调用锁定库存功能
                R r = wareFeignService.orderLock(lockVo);
                if (r.getCode() == 0) {
                    // 锁定成功
                    return res;
                }else {
                    // 失败
                    res.setCode(3);
                    return res;
                }

            }

        }

        res.setCode(2);
        return res;
    }

    /**
     * 保存订单数据
     * @param order 订单
     */
    private void saveOrderAndItems(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime( new Date());

        this.save(orderEntity);
        orderItemService.saveBatch(order.getOrderItems());
    }

    /**
     *  创建订单（总）
     * @return
     */
    private OrderCreateTo createOrder(){
        OrderCreateTo order = new OrderCreateTo();
        // 设置订单ID
        String  orderSn =  IdWorker.getTimeId();

        // 1.构建基本订单数据
        OrderEntity orderEntity = buildOrder(orderSn);

         // 2、获取购物车中的所有选中购物项
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn);

        // 3.验证价格
        computePrice(orderEntity, orderItems);

        order.setOrder(orderEntity);
        order.setOrderItems(orderItems);
        return order;
    }

    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;   // 订单总金额

        BigDecimal promotionAmount = BigDecimal.ZERO;   // 商品促销分解金额
        BigDecimal integrationAmount = BigDecimal.ZERO; // 积分优惠分解金额
        BigDecimal couponAmount = BigDecimal.ZERO;  // 优惠券优惠分解金额

        int integration = 0;    // 赠送积分
        int growth = 0; // 赠送成长值

        for (OrderItemEntity orderItem : orderItems) {
            totalAmount = totalAmount.add(orderItem.getRealAmount());
            promotionAmount = promotionAmount.add(orderItem.getPromotionAmount());
            integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount());
            couponAmount = couponAmount.add(orderItem.getCouponAmount());
            integration += orderItem.getGiftIntegration();
            growth += orderItem.getGiftGrowth();
        }

        order.setTotalAmount(totalAmount);
        order.setFreightAmount(totalAmount.compareTo(OrderConstant.FREE_FREIGHT_PRICE) >= 0 ? BigDecimal.ZERO
                : OrderConstant.FREIGHT);
        order.setPayAmount(totalAmount.add(order.getFreightAmount()));
        order.setPromotionAmount(promotionAmount);
        order.setIntegrationAmount(integrationAmount);
        order.setCouponAmount(couponAmount);
        order.setIntegration(integration);
        order.setGrowth(growth);
    }

    /**
     * 构建订单基本数据
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        OrderSubmitVO orderSubmitVO = threadLocal.get();
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();



        // 设置收货人信息
        orderEntity.setFreightAmount(new BigDecimal(10));
        orderEntity.setReceiverCity("郑州");
        orderEntity.setReceiverDetailAddress("zz");
        orderEntity.setReceiverPhone("123");
        orderEntity.setReceiverName("gmy");
        orderEntity.setReceiverProvince("HeNan");
        orderEntity.setReceiverPostCode("450003");
        orderEntity.setReceiverRegion("HeBei");

        orderEntity.setOrderSn(orderSn);
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setMemberId(memberResponseVo.getId());
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.TO_BE_PAID.getCode());
        return orderEntity;
    }



    /**
     * 根据购物车的选中项创建订单项
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 这个方法会更新购物车中所有商品的最新价格，这也是下单流程中最后一次确定购物项的价格了
        List<OrderConfirmVo.OrderItemVO> OrderItemVOs = cartFeignService.getCurrentUserCartItems();
        if (CollectionUtils.isNotEmpty(OrderItemVOs)) {
            List<OrderItemEntity> orderItems = OrderItemVOs.stream().map(item -> {
                // 创建某一个订单的购物项
                OrderItemEntity orderItem = this.buildOrderItem(item);
                orderItem.setOrderSn(orderSn);
                return orderItem;
            }).collect(Collectors.toList());
            return orderItems;
        }
        return null;
    }

    /**
     * 构建某一个订单购物项
     *
     * @param orderItemVO
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderConfirmVo.OrderItemVO orderItemVO) {
        OrderItemEntity orderItem = new OrderItemEntity();
//        orderItem.setOrderId(0L);

        // 商品的SPU信息
        R r = productFeignService.getSpuInfoBySkuId(orderItemVO.getSkuId());
        if (r.getCode() == 0) {
            SpuInfoDTO spuInfoDTO = r.getData(new TypeReference<SpuInfoDTO>() {
            });
            orderItem.setSpuId(spuInfoDTO.getId());
            orderItem.setSpuName(spuInfoDTO.getSpuName());
            orderItem.setSpuBrand(spuInfoDTO.getBrandId().toString());
            orderItem.setCategoryId(spuInfoDTO.getCategoryId());
        }

        // 商品的sku信息
        orderItem.setSkuId(orderItemVO.getSkuId());
        orderItem.setSkuName(orderItemVO.getTitle());
        orderItem.setSkuPic(orderItemVO.getImage());
        orderItem.setSkuPrice(orderItemVO.getPrice());
        orderItem.setSkuQuantity(orderItemVO.getCount());

        // 将销售属性的List转换为String
        StringBuilder listStr = new StringBuilder();
        for (String attr : orderItemVO.getSkuAttr()) {
            listStr.append(attr + ";");
        }
        orderItem.setSkuAttrsVals(listStr.toString());


        // 优惠信息[不做，直接赋0]
        orderItem.setCouponAmount(BigDecimal.ZERO);
        orderItem.setPromotionAmount(BigDecimal.ZERO);
        orderItem.setIntegrationAmount(BigDecimal.ZERO);

        // 最终价格：总额 - 所有优惠
        BigDecimal totalPrice = orderItemVO.getPrice().multiply(new BigDecimal(orderItemVO.getCount()));
        BigDecimal realAmount = totalPrice.subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getPromotionAmount()).subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(realAmount);

        // 积分信息
        orderItem.setGiftIntegration(totalPrice.intValue());
        orderItem.setGiftGrowth(totalPrice.intValue());

        return orderItem;
    }


    /**
     * 监听消息 @RabbitListener 注解
     * 可以写的参数类型：
     *  1。Message  消息头 + 消息体
     *  2. T<发送的消息类型>  OrderEntity()
     *  3。Channel channel 当前传输的通道
     *  Queue：可以多人来监听，但是只有一个能收到消息。
     *      场景：
     *          1。订单服务启动多个，同一个消息，只能有1个客户端收到
     *          2。只有一个消息完全处理完（方法运行完），才会接受下一个消息
     * @param obj
     */
    @RabbitHandler
    @RabbitListener(queues = {"my-queue"})
    public void acceptMessage(Message obj){
        // 消息体
        byte[] body = obj.getBody();
        // 消息头
        MessageProperties messageProperties = obj.getMessageProperties();
        System.out.println("接收到消息了: -->" + obj);
    }


}