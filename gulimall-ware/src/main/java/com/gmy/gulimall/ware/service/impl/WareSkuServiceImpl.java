package com.gmy.gulimall.ware.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.ware.dao.WareSkuDao;
import com.gmy.gulimall.ware.entity.WareSkuEntity;
import com.gmy.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        final LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

        final String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)){
            wrapper.eq(WareSkuEntity::getSkuId, skuId);
        }
        final String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)){
            wrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}