package com.gmy.gulimall.coupon.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.gmy.common.to.MemberPrice;
import com.gmy.common.to.SkuReductionTo;
import com.gmy.common.utils.R;
import com.gmy.gulimall.coupon.entity.MemberPriceEntity;
import com.gmy.gulimall.coupon.entity.SkuLadderEntity;
import com.gmy.gulimall.coupon.service.MemberPriceService;
import com.gmy.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.coupon.dao.SkuFullReductionDao;
import com.gmy.gulimall.coupon.entity.SkuFullReductionEntity;
import com.gmy.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 1. 保存满减打折，会员价格
        final SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setAddOther(skuReductionTo.getPriceStatus());

        if (skuLadderEntity.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        // 2. 满减信息
        final SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){

            this.save(skuFullReductionEntity);
        }

        //3. 会员价格
        final List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if (CollectionUtils.isNotEmpty(memberPrice)){
            final List<MemberPriceEntity> collect = memberPrice.stream()
                    .map(item -> {
                        MemberPriceEntity price = new MemberPriceEntity();
                        price.setAddOther(skuReductionTo.getPriceStatus());
                        price.setSkuId(skuReductionTo.getSkuId());
                        price.setMemberLevelId(item.getId());
                        price.setMemberLevelName(item.getName());
                        price.setMemberPrice(item.getPrice());
                        return price;
                    }).filter(item -> item.getMemberPrice().compareTo(new BigDecimal("0")) == 1)
                    .collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }

        return R.ok();
    }

}