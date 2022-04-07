package com.gmy.gulimall.search.service;

import com.gmy.common.to.es.SkuESModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSaveService {
    /**
     * 上架商品
     *
     * @param skuESModuleList 上架商品的信息
     */
    Boolean up(List<SkuESModule> skuESModuleList) throws IOException;
}
