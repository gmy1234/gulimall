package com.gmy.common.vo.mq;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author UnityAlvin
 * @date 2021/7/29 15:10
 */
@Data
public class SeckillSkuDTO {
    private String orderSn;
    private Long memberId;
    private Long promotionSessionId;    // 场次id
    private Long skuId; // skuId
    private Integer num;    // 秒杀数量
    private Integer integration;       // 积分
    private BigDecimal totalPrice;   // 订单总额
    private BigDecimal payPrice;    // 应付价格
    private String orderToken;      // 防重令牌：防止用户重复提交订单
    private BigDecimal freight;  // 运费
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;   // 秒杀单价
    private String randomCode;
}
