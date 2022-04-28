package com.gmy.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.ware.entity.WareSkuEntity;
import com.gmy.gulimall.ware.vo.LockStockResultVo;
import com.gmy.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:48:48
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询是否有库存数
     * @param skuIds skuId
     * @return 集合
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 为某个订单锁库存
     * @param vo
     * @return
     */
    List<LockStockResultVo> lockCount(WareSkuLockVo vo);
}

