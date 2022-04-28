package com.gmy.gulimall.ware.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单确认 页面 需要的数据
 */

public class OrderConfirmVo {
    @Setter @Getter
    private List<MemberAddressVO> address;
    @Setter @Getter
    private List<OrderItemVO> items;    // 购物车的所有商品
    @Setter @Getter
    private Integer integration;       // 积分

    //private BigDecimal totalPrice;   // 订单总额
    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVO item : items) {
                BigDecimal price = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                total = total.add(price);
            }
        }
        return total;
    }

    // 应付价格
    public BigDecimal getPayPrice(){
        return this.getTotalPrice();
    }

    @Setter @Getter
    private String orderToken;      // 防重令牌：防止用户重复提交订单
    @Setter @Getter
    private Integer totalCount;       // 总件数
    @Setter @Getter
    private Map<Long, Boolean> stocks;   // 所有商品库存信息
    @Setter @Getter
    private BigDecimal freight;  // 运费

    /**
     * 会员地址信息
     */
    @Data
    public static class MemberAddressVO {
        private Long id;
        private Long memberId;          // member_id
        private String name;            // 收货人姓名
        private String phone;           // 电话
        private String postCode;        // 邮政编码
        private String province;        // 省份/直辖市
        private String city;            // 城市
        private String region;          // 区
        private String detailAddress;   // 详细地址(街道)
        private String areaCode;        // 省市区代码
        private Integer defaultStatus;  // 是否默认
    }

    /**
     * 购物车内的商品数据
     */
    @Data
    public static class OrderItemVO {
        private Long skuId;
        private String title;
        private String image;
        private List<String> skuAttr;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;
        private BigDecimal weight;      // 商品重量
    }
}
