package com.gmy.gulimall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    // 关键字
    private String keyword;

    // 三级分类的ID
    private Long catalog3Id;

    /**
     * saleCount（销量）、hotScore（热度分）、skuPrice（价格）
     */
    // 排序条件
    private String sort;

    /**
     * 过滤条件 hasStock、skuPrice区间、brandId 品牌ID、catalog3Id、attrs商品的属性
     */
    private Integer hasStock;
    // 商品的 价格区间
    private String skuPrice;
    // 商品的品牌 ID
    private List<Long> brandId;
    // 商品的属性筛选
    private List<String> attrs;

    private Integer pageNum;
}
