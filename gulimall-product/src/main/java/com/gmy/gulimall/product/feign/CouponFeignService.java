package com.gmy.gulimall.product.feign;

import com.gmy.common.to.SkuReductionTo;
import com.gmy.common.to.SpuBoundTo;
import com.gmy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 调用优惠卷服务
 */
@FeignClient(name = "gulimall-coupon")
public interface CouponFeignService {


    @PostMapping(value = "/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping(value = "/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
