package com.gmy.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.CategoryDao;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 以树形结构获取商品分类
     *
     * @return list集合
     */
    @Override
    public List<CategoryEntity> getAllCategoryWithTree() {

        // 获取所有的分类
        final List<CategoryEntity> categorys = this.baseMapper.selectList(null);
        // 获取一级分类
        final List<CategoryEntity> level1Menus = categorys.stream()
                // 过滤出 根菜单
                .filter(category -> category.getParentCid() == 0)
                // 找出所有的子菜单
                .map(menu -> {
                    menu.setChildren(getChildren(menu, categorys));
                    return menu;
                })
                // 排序
                .sorted(Comparator.comparingInt(menu ->
                        menu.getSort() == null ? 0 : menu.getSort())
                )
                // 收集
                .collect(Collectors.toList());

        return level1Menus;
    }

    /**
     * 递归查询当前菜单的子菜单
     *
     * @param root 当前菜单
     * @param all  所有的菜单
     * @return 获取当前菜单的子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        final List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid().equals(root.getCatId());
                })
                // 递归找子菜单
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(menu ->
                        menu.getSort() == null ? 0 : menu.getSort()))
                .collect(Collectors.toList());

        return children;
    }

}