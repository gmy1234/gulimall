package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取当前品牌关联的所有分类
     *
     * @param brandId 当前品牌Id
     * @return 所有分类
     */
    List<CategoryBrandRelationEntity> getRelationCategory(Long brandId);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 级联更新 品牌
     * @param brandId 品牌Id
     * @param name 品牌名
     */
    void updateBrand(Long brandId, String name);

    /**
     * 级联更新 分类
     * @param catId 分类Id
     * @param name 分类名
     */
    void updateCategory(Long catId, String name);
}

