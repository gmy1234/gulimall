package com.gmy.guliorder.order.vo;


import com.gmy.guliorder.order.entity.OrderEntity;
import lombok.Data;

/**
 * 提交订单之后，给页面返回的数据
 * @author UnityAlvin
 * @date 2021/7/18 10:11
 */
@Data
public class OrderSubmitResponseVO {
    private OrderEntity order;
    private Integer code; // 错误校验码
}
