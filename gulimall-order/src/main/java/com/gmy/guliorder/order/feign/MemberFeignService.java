package com.gmy.guliorder.order.feign;

import com.gmy.guliorder.order.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<OrderConfirmVo.MemberAddressVO> getAddressById(@PathVariable("memberId") Long memberId);

}
