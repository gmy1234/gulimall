package com.gmy.guliorder.order.feign;

import com.gmy.common.utils.R;
import com.gmy.guliorder.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @PostMapping("/ware/waresku/lock")
    R orderLock(WareSkuLockVo vo);
}
