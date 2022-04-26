package com.gmy.guliorder.order.feign;

import com.gmy.guliorder.order.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/currentUserCartItems")
    List<OrderConfirmVo.OrderItemVO> getCurrentUserCartItems();
}
