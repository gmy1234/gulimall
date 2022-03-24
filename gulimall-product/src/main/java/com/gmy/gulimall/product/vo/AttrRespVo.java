package com.gmy.gulimall.product.vo;

import lombok.Data;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/24 13:18
 */

@Data
public class AttrRespVo extends AttrVo {

    /**
     * 所属分类名字
     */
    private String catelogName;

    /**
     * 所属分组名字
     */
    private String groupName;

    /**
     * 分类属性的路径
     */
    private Long[] catelogPath;
}
