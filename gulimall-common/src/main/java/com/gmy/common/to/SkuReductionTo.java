package com.gmy.common.to;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/28 13:27
 */
@Data
public class SkuReductionTo {

    private Long skuId;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;

}
