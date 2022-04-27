package com.gmy.guliorder.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 点击提交订单，页面传过来的数据
 * @author UnityAlvin
 * @date 2021/7/18 8:58
 */
@Data
public class OrderSubmitVO {
    private Long addrId;
    private String orderToken;
    private BigDecimal payPrice;
}
