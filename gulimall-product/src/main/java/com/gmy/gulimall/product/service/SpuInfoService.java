package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.SpuInfoEntity;
import com.gmy.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  发布商品
     * @param skuInfoVo 商品的信息
     */
    void saveSpuInfo(SpuSaveVo skuInfoVo);

    /**
     *  基本信息
     * @param spuBasicInfo 基本信息实体
     */
    void saveBasicInfo(SpuInfoEntity spuBasicInfo);

    PageUtils queryPageMyCondition(Map<String, Object> params);

    /**
     * 商品上架功能
     *
     * @param spuId spu 的ID
     */
    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

