package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.AttrGroupEntity;
import com.gmy.gulimall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    /**
     *  根据分类 Id 获取分类下所有的分组和和分组里的属性
     * @param catelogId 分类的Id
     * @return 分类和属性
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

