package com.gmy.guliorder.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gmy.guliorder.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 20:26:51
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
