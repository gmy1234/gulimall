package com.gmy.common.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class StockLockedTo {
    // 库存工作单ID
    private Long id;

    private Long detailId;
}
