package com.gmy.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

public class OrderConstant {

    public static final String USER_TOKEN_PREFIX = "order_token";

    public static final BigDecimal FREE_FREIGHT_PRICE = new BigDecimal(99);  // 默认包邮金额
    public static final BigDecimal FREIGHT = new BigDecimal(8);  // 默认运费
    public static final String ORDER_TOKEN_PREFIX = "gulimall:order:token:";

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";   // 交换机

    public static final String ORDER_DELAY_QUEUE = "order.delay.queue"; // 队列1
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue"; // 队列2
    public static final String ORDER_SECKILL_ORDER_QUEUE = "order.seckill.order.queue"; // 队列4


    public static final String ORDER_CREATE_ORDER_ROUTING_KEY = "order.create.order";   // 路由键1
    public static final String ORDER_RELEASE_ORDER_ROUTING_KEY = "order.release.order"; // 路由键2
    public static final String ORDER_RELEASE_OTHER_ROUTING_KEY = "order.release.other"; // 路由键3
    public static final String ORDER_SECKILL_ORDER_ROUTING_KEY = "order.seckill.order"; // 路由键4
    public static final String ORDER_SECKILL_RELEASE_OTHER_ROUTING_KEY= "order.seckill.release.other";   // 绑定关系

    public static final Long MESSAGE_TTL = 60000L;  // 消息等待时间

    public static final String ORDER_RELEASE_OTHER_BINDING = "order.release.other.#";   // 绑定关系
    public static final String ORDER_SECKILL_RELEASE_OTHER_BINDING = "order.seckill.release.other.#";   // 绑定关系

    @Getter
    @AllArgsConstructor
    public enum OrderStatusEnum {
        TO_BE_PAID(0, "待付款"),
        PAID(1, "已付款"),
        DELIVERED(2, "已发货"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消"),
        SERVING(5, "售后中"),
        SERVICE_COMPLETED(6, "售后完成");

        private int code;
        private String msg;
    }
}
