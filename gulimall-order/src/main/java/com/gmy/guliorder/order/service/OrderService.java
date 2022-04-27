package com.gmy.guliorder.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.guliorder.order.entity.OrderEntity;
import com.gmy.guliorder.order.vo.OrderConfirmVo;
import com.gmy.guliorder.order.vo.OrderSubmitResponseVO;
import com.gmy.guliorder.order.vo.OrderSubmitVO;


import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 20:26:51
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单
     * @param vo
     * @return
     */
    OrderSubmitResponseVO submitOrder(OrderSubmitVO vo);
}

