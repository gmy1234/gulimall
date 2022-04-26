package com.gmy.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.StringUtils;
import com.gmy.common.utils.R;
import com.gmy.gulimall.cart.exception.CartExceptionHandler;
import com.gmy.gulimall.cart.feign.ProductFeignService;
import com.gmy.gulimall.cart.interceptor.CartInterceptor;
import com.gmy.gulimall.cart.service.CartService;
import com.gmy.gulimall.cart.to.UserInfoTo;
import com.gmy.gulimall.cart.vo.CartItemVo;
import com.gmy.gulimall.cart.vo.CartVo;
import com.gmy.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService{

    private final String CART_PREFIX = "gulimall:cart";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    /**
     * // 获取到要操作的购物车
     * @return 购物车的操作
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 肯定是用户登陆的
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        // 绑定 key ，操作对应的key 的 value
        return redisTemplate.boundHashOps(cartKey);
    }

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 获取到要操作的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String redisData = (String) cartOps.get(skuId.toString());
        // 判断购物车里是否有数据。
        if (StringUtils.isBlank(redisData)) {
            // 购物车无此商品，
            CartItemVo cartItemVo = new CartItemVo();

            // 开启第一个异步编排，查询SKU信息和 sku的组合信息
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                // 远程调用接口，通过ID 查询 sku信息
                R info = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>(){});
                System.out.println(skuInfo);
                // 2.商品添加到 购物车
                cartItemVo.setSkuId(skuId);
                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                if (skuInfo.getSkuDefaultImg() != null){
                    cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                }
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setPrice(skuInfo.getPrice());
            }, executor);

            // 开启第二个异步编排.封装远程调用查询 sku的组合信息。
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() ->{
                // 远程查询 SKU的组合信息
                List<String> values = productFeignService.getSkuAttrValuesBySkuId(skuId);
                if (values != null && values.size() > 0) {
                    cartItemVo.setSkuAttrValues(values);
                }else {
                    cartItemVo.setSkuAttrValues(new ArrayList<>());
                }
            }, executor);

            // 等待异步任务完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuSaleAttrValues).get();

            // 购车的数据保存到 Redis里
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), s);
            return cartItemVo;

        }else {
            // 购物车 有商品
            CartItemVo item = JSON.parseObject(redisData, CartItemVo.class);
            item.setCount(item.getCount() + num);
            // 更新 redis
            cartOps.put(skuId.toString(), JSON.toJSONString(item));
            return item;
        }
    }



    @Override
    public CartItemVo getCartItem(Long skuId) {
        //拿到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(redisValue, CartItemVo.class);
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 1、登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            // 临时购物车的键
            String temptCartKey = CART_PREFIX + userInfoTo.getUserKey();

            //2、如果临时购物车的数据还未进行合并
            List<CartItemVo> tempCartItems = getCartItems(temptCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清除临时购物车的数据
                clearCartInfo(temptCartKey);
            }

            //3、获取登录后的购物车数据【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);

        } else {
            //没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车里面的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;
    }

    /**
     * 获取购物车里面的数据
     *
     * @param cartKey
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            return values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void clearCartInfo(String cartKey) {


    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setCheck(check == 1);

        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), redisValue);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        //查询购物车里面的商品
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), redisValue);
    }

    /**
     * 删除购物项
     *
     * @param skuId 商品的 ID
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());

    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //获取当前用户登录的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //如果用户未登录直接返回null
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            //获取购物车项
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //获取所有的
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                throw new CartExceptionHandler();
            }
            // 筛选出选中的
            cartItemVoList = cartItems.stream()
                    .filter(CartItemVo::getCheck)
                    .peek(item -> {
                        // 更新为最新的价格（查询数据库）
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                    }).collect(Collectors.toList());
        }

        return cartItemVoList;
    }
}
