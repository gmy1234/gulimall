package com.gmy.gulimall.coupon.dao;

import com.gmy.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:12:49
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
