package com.gmy.gulimall.search.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.gmy.common.constant.EsConstant;
import com.gmy.gulimall.search.GulimallSearchApplication;
import com.gmy.gulimall.search.config.GulimallElasticsearchConfig;
import com.gmy.gulimall.search.service.MallSearchService;
import com.gmy.gulimall.search.vo.SearchParam;
import com.gmy.gulimall.search.vo.SearchRes;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.directory.SearchResult;
import java.io.IOException;

public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchRes search(SearchParam searchKey) {
        // 动态构建出查询需要的DSL语句
        SearchResult searchResult = null;

        // 1。准备检索请求
        SearchRequest searchRequest = buildSearchRequest(searchKey);

        try {
            SearchResponse response = client.search(searchRequest,
                    GulimallElasticsearchConfig.COMMON_OPTIONS);

            searchResult = buildSearchResult();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 构建检索请求
     *
     * @return 构建检索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /**
         * 查询：模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        //1. 构建 bool-query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //1.1 bool-must 模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        //1.2.1 bool-filter catalogId 按照三级分类id查询
        if (null != param.getCatalog3Id()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //1.2.2 bool-filter brandId 按照品牌id查询
        if (null != param.getBrandId() && param.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        //1.2.3 bool-filter attrs 按照指定的属性查询
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            param.getAttrs().forEach(item -> {
                //attrs=1_5寸:8寸&2_16G:8G
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

                //attrs=1_5寸:8寸
                String[] s = item.split("_");
                String attrId = s[0]; // 检索的属性id
                String[] attrValues = s[1].split(":");//这个属性检索用的值
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

                // 每一个属性都要生成一个 nested 查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            });

        }
        //1.2.4 bool-filter hasStock 按照是否有库存查询
        if (null != param.getHasStock()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        //1.2.5 skuPrice bool-filter 按照价格区间查询
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            //skuPrice形式为：1_500或_500或500_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] price = param.getSkuPrice().split("_");
            if (price.length == 2) {
                rangeQueryBuilder.gte(price[0]).lte(price[1]);
            } else if (price.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(price[1]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(price[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        // 封装所有的查询条件
        searchSourceBuilder.query(boolQueryBuilder);





        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }

    /**
     * 构建检索结果
     *
     * @return 检索结果
     */
    private SearchResult buildSearchResult() {
        return null;
    }
}
