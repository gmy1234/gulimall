package com.gmy.guliorder.order.vo;

import com.gmy.guliorder.order.entity.OrderEntity;
import com.gmy.guliorder.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;

    private BigDecimal fare; // 运费
}
