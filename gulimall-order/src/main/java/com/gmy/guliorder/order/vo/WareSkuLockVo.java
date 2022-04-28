package com.gmy.guliorder.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {

    private String orderSn;

    private List<OrderConfirmVo.OrderItemVO> locks; // 需要锁住的库存信息
}
