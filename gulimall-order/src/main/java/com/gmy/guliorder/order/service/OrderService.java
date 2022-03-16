package com.gmy.guliorder.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.guliorder.order.entity.OrderEntity;


import java.util.Map;

/**
 * 订单
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 20:26:51
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

