package com.gmy.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:12:49
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

