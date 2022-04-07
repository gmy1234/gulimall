package com.gmy.gulimall.search.controller;

import com.gmy.common.to.es.SkuESModule;
import com.gmy.common.utils.R;
import com.gmy.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;

    @RequestMapping("/product")
    public R productStatusUp(List<SkuESModule> skuESModuleList){

        productSaveService.up(skuESModuleList);

        return R.ok();
    }
}
