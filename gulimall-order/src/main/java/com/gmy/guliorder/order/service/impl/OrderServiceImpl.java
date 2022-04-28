package com.gmy.guliorder.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.gmy.common.constant.OrderConstant;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.common.utils.R;
import com.gmy.common.vo.MemberResponseVo;
import com.gmy.guliorder.order.dao.OrderDao;
import com.gmy.guliorder.order.entity.OrderEntity;
import com.gmy.guliorder.order.feign.CartFeignService;
import com.gmy.guliorder.order.feign.MemberFeignService;
import com.gmy.guliorder.order.feign.WareFeignService;
import com.gmy.guliorder.order.interceptor.LoginUserInterceptor;
import com.gmy.guliorder.order.service.OrderService;
import com.gmy.guliorder.order.vo.OrderConfirmVo;
import com.gmy.guliorder.order.vo.OrderCreateTo;
import com.gmy.guliorder.order.vo.OrderSubmitResponseVO;
import com.gmy.guliorder.order.vo.OrderSubmitVO;
import org.aspectj.weaver.ast.Or;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

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
    public OrderSubmitResponseVO submitOrder(OrderSubmitVO vo) {
        MemberResponseVo member = LoginUserInterceptor.loginUser.get();
        OrderSubmitResponseVO res = new OrderSubmitResponseVO();
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

        }

        res.setCode(2);
        return res;
    }

    private OrderCreateTo createOrder(){
        OrderCreateTo order = new OrderCreateTo();

        return order;
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