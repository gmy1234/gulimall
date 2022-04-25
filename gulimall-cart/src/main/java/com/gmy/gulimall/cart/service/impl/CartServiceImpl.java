package com.gmy.gulimall.cart.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.gmy.common.utils.R;
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

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService{

    private final String CART_PREFIX = "gulimall:cart";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

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

        // 添加商品的时候，判断Redis 里边是否有 该商品，有的话数量 + num
        return redisTemplate.boundHashOps(cartKey);
    }

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 获取到要操作的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        // 远程调用接口，通过ID 查询 sku信息
        R info = productFeignService.getSkuInfo(skuId);
        SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>(){});
        // 2.商品添加到 购物车
        CartItemVo cartItemVo = new CartItemVo();
        cartItemVo.setSkuId(skuId);
        cartItemVo.setCheck(true);
        cartItemVo.setCount(1);
        cartItemVo.setImage(skuInfo.getSkuDefaultImg());
        cartItemVo.setTitle(skuInfo.getSkuTitle());
        cartItemVo.setPrice(skuInfo.getPrice());
        // 远程查询 SKU的组合信息
        cartItemVo.setSkuAttrValues(null);



        return null;
    }



    @Override
    public CartItemVo getCartItem(Long skuId) {
        return null;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public void clearCartInfo(String cartKey) {

    }

    @Override
    public void checkItem(Long skuId, Integer check) {

    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {

    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {

    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        return null;
    }
}
