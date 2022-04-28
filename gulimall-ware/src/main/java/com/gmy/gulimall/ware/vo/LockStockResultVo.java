package com.gmy.gulimall.ware.vo;

import lombok.Data;

@Data
public class LockStockResultVo {

    private Long SkuId;

    private Integer num;

    private boolean locked;
}

