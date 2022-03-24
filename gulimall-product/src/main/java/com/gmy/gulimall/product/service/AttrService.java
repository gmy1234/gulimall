package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.AttrEntity;
import com.gmy.gulimall.product.vo.AttrRespVo;
import com.gmy.gulimall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存基本信息 和 分组信息
     * @param attr 信息实体
     */
    void saveAttr(AttrVo attr);

    /**
     * 查询规格参数列表
     * @param params 模糊查询
     * @param categoryId 分类Id
     * @param type 销售属性 还是 基本属性
     * @return 集合
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId, String type);

    /**
     * 规格参数 修改时回显信息，
     * @param attrId 属性Id
     * @return 信息
     */
    AttrRespVo getAttrInfo(Long attrId);

    void updateAttrVo(AttrVo attrVo);
}

