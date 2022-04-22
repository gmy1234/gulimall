package com.gmy.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.gmy.common.utils.R;
import com.gmy.gulimall.cart.service.CartService;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service("cartService")
public class CartServiceImpl implements CartService{


    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
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
