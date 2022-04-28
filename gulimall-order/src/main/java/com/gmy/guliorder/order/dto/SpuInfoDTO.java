package com.gmy.guliorder.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询的spuInfo
 * @author UnityAlvin
 * @date 2021/7/18 19:25
 */
@Data
public class SpuInfoDTO {

    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long categoryId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     *
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    private Integer publishStatus;
    private Date createTime;
    private Date updateTime;
}
