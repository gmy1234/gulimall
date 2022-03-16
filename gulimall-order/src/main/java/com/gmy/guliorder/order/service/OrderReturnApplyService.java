package com.gmy.guliorder.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.guliorder.order.entity.OrderReturnApplyEntity;


import java.util.Map;

/**
 * 订单退货申请
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 20:26:51
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

