package com.gmy.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gmy.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author gmyDL
 * @Date 2022/3/25 21:42
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * AttrEntity
     */
    private List<AttrEntity> attrs;
}
