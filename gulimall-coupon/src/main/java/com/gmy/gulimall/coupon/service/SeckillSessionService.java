package com.gmy.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:12:49
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

