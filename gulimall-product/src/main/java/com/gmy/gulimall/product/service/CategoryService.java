package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.vo.Catalogs2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 以树形结构获取商品分类
     *
     * @return list集合
     */
    List<CategoryEntity> getAllCategoryWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到catelogId 的完整路径
     * 父/子/孙子
     * @param catalogId 分类Id
     * @return 路径
     */
    Long[] findCatalogPath(Long catalogId);

    /**
     * 级联更新 分类名
     * @param category 分类的实体
     */
    void updateCascade(CategoryEntity category);


    /**
     * 查询一级分类
     * @return 一级分类的实体
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     * 单纯的查数据库
     * @return 分类结果
     */
    Map<String, List<Catalogs2Vo>> getCatalogDataFromDB();

    /**
     *二级、三级分类数据
     * 本地锁
     * @return 分类的数据
     */
    Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithLocalLock();

    /**
     * 使用redis 缓存来封装分类
     * @return 分类
     */
    Map<String, List<Catalogs2Vo>> getCatalogJsonFromRedis();


    /**
     * Redis锁
     * @return 分类结果
     */
    Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithRedisLock();


    /**
     * 使用 Redisson 锁
     * @return 分类结果
     */
    Map<String, List<Catalogs2Vo>> getCatalogJsonFromDBWithRedissonLock();

}

