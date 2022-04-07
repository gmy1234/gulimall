package com.gmy.gulimall.search.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.gmy.common.constant.EsConstant;
import com.gmy.common.to.es.SkuESModule;
import com.gmy.gulimall.search.config.GulimallElasticsearchConfig;
import com.gmy.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean up(List<SkuESModule> skuESModuleList) throws IOException {

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
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);

        // 商品上架是否错误
        boolean failures = bulk.hasFailures();
        if (failures){

            List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId)
                    .collect(Collectors.toList());

            log.error("商品上架错误", collect);

        }
        return failures;

    }
}
