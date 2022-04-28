package com.gmy.guliorder.order.feign;

import com.gmy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("/product/spuinfo/info/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
