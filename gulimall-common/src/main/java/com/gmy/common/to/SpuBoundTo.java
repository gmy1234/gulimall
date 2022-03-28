package com.gmy.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/28 13:12
 */
@Data
public class SpuBoundTo {

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;
}
