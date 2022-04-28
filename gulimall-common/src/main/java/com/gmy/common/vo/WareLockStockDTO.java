package com.gmy.common.vo;


import lombok.Data;

import java.util.List;

/**
 * 锁定库存需要传输的数据
 * @author UnityAlvin
 * @date 2021/7/19 9:14
 */
@Data
public class WareLockStockDTO {
    private String  orderSn;
    private List<OrderItemDTO> items;

    /**
     * 购物车内的商品数据
     */
    @Data
    public static class OrderItemDTO {
        private Long skuId;
        private String skuName;
        private Integer skuQuantity;
    }
}
