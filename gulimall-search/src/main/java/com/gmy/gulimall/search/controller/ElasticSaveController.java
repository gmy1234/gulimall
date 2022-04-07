package com.gmy.gulimall.search.controller;

import com.gmy.common.exception.BizCodeEnume;
import com.gmy.common.to.es.SkuESModule;
import com.gmy.common.utils.R;
import com.gmy.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @RequestMapping("/product")
    public R productStatusUp(List<SkuESModule> skuESModuleList) {

        Boolean up;
        try {
            up = productSaveService.up(skuESModuleList);
        } catch (IOException e) {

            log.error("ElasticSaveController 商品上架错误", e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),
                    BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (up) {
            return R.error();
        }
        return R.ok();
    }
}
