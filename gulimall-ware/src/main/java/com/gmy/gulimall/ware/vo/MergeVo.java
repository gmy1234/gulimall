package com.gmy.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/29 13:06
 */
@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;
}
