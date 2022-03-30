package com.gmy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.product.dao.BrandDao;
import com.gmy.gulimall.product.dao.CategoryDao;
import com.gmy.gulimall.product.entity.BrandEntity;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.CategoryBrandRelationDao;
import com.gmy.gulimall.product.entity.CategoryBrandRelationEntity;
import com.gmy.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationDao relationDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取当前品牌关联的所有分类
     *
     * @param brandId 当前品牌Id
     * @return 所有分类
     */
    @Override
    public List<CategoryBrandRelationEntity> getRelationCategory(Long brandId) {
        return this.baseMapper.selectList(
                new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                        .eq(CategoryBrandRelationEntity::getBrandId, brandId)
        );
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        final Long brandId = categoryBrandRelation.getBrandId();
        final Long catelogId = categoryBrandRelation.getCatelogId();

        // 查询品牌名字
        final BrandEntity brandEntity = brandDao.selectById(brandId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        // 查询分类名字
        final CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.baseMapper.insert(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        final CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setBrandId(brandId);
        categoryBrandRelation.setBrandName(name);
        final LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);

        this.update(categoryBrandRelation, wrapper);
    }

    @Transactional
    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId, name);
    }

    /**
     * 根据 分类 ID 查询 指定的品牌信息
     * @param catId 分类Id
     * @return 品牌信息
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        final LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryBrandRelationEntity::getCatelogId, catId);
        final List<CategoryBrandRelationEntity>  cateBrandRelation = relationDao.selectList(wrapper);

        // 这里只获取了 分类和品牌关系表的关系数据，我们要返回 品牌整个数据，所以用 流 映射了一下
        return cateBrandRelation.stream()
                .map(item -> brandService.getById(item.getBrandId()))
                .collect(Collectors.toList());
    }

}