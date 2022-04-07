package com.gmy.gulimall.product.feign;


import com.gmy.common.to.es.SkuESModule;
import com.gmy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    public R productStatusUp(List<SkuESModule> skuESModuleList);


}
