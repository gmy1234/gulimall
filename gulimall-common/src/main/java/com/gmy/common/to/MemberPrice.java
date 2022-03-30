package com.gmy.common.to;

import lombok.Data;

import java.math.BigDecimal;



/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/26 12:57
 */
@Data
public class MemberPrice {

    private Long id;

    private String name;

    private BigDecimal price;
}
