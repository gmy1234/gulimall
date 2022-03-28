package com.gmy.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.product.entity.SpuInfoEntity;
import com.gmy.gulimall.product.vo.SpuSaveVo;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.SkuInfoDao;
import com.gmy.gulimall.product.entity.SkuInfoEntity;
import com.gmy.gulimall.product.service.SkuInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageMyCondition(Map<String, Object> params) {

        final LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        /**
         *         key:
         *         catelogId: 0
         *         brandId: 0
         *         min: 0
         *         max: 0
         */
        final String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> {
                w.eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
            });
        }
        final String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equals(brandId)) {
            wrapper.eq(SkuInfoEntity::getBrandId, brandId);
        }
        final String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equals(brandId)) {
            wrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
        }
        final String min = (String) params.get("min");
        if (StringUtils.isNotBlank(min) && !"0".equals(min)) {
            wrapper.ge(SkuInfoEntity::getPrice, min);
        }
        final String max = (String) params.get("max");
        if (StringUtils.isNotBlank(max) && !"0".equals(max)) {
            wrapper.le(SkuInfoEntity::getPrice, max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params), wrapper
        );

        return new PageUtils(page);
    }


}