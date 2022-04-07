package com.gmy.gulimall.search.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.gmy.common.constant.EsConstant;
import com.gmy.common.to.es.SkuESModule;
import com.gmy.gulimall.search.config.GulimallElasticsearchConfig;
import com.gmy.gulimall.search.service.ProductSaveService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public void up(List<SkuESModule> skuESModuleList) throws IOException {

        // 保存到es
        // 1、建立 es 映射关系

        // 2、给 es 中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuESModule skuESModule : skuESModuleList) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuESModule.getSkuId().toString());
            String s = JSONUtils.toJSONString(skuESModule);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        restHighLevelClient.bulk(bulkRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);

    }
}
