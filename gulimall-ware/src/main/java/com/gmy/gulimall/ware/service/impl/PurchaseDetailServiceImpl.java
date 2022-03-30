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

import com.gmy.gulimall.ware.dao.PurchaseDetailDao;
import com.gmy.gulimall.ware.entity.PurchaseDetailEntity;
import com.gmy.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        final LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

        final String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)){
            wrapper.and( w->{
                w.eq(PurchaseDetailEntity::getPurchaseId, key).or()
                        .eq(PurchaseDetailEntity::getSkuId, key);
            });
        }

        final String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)){
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }

        final String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)){
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}