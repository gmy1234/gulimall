package com.gmy.common.vo.mq;

import lombok.Data;

/**
 * @author UnityAlvin
 * @date 2021/7/23 16:19
 */
@Data
public class StockLockDTO {
    private Long id;  // 库存工作单的id
    private StockLockDetailDTO stockLockDetailDTO;    // 所有库存工作单详情的id

    @Data
    public static class StockLockDetailDTO {
        private Long id;
        /**
         * sku_id
         */
        private Long skuId;
        /**
         * sku_name
         */
        private String skuName;
        /**
         * 购买个数
         */
        private Integer skuNum;
        /**
         * 工作单id
         */
        private Long taskId;

        private Long wareId;    // 库存id
        private Integer lockStatus; // 锁定状态
    }

}
