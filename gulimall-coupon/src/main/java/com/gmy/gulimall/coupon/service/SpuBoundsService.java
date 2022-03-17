package com.gmy.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:12:49
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

