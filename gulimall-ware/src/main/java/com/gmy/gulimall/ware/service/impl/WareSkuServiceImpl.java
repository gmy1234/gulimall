package com.gmy.gulimall.ware.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.gulimall.ware.exception.NoStockException;
import com.gmy.gulimall.ware.vo.LockStockResultVo;
import com.gmy.gulimall.ware.vo.OrderConfirmVo;
import com.gmy.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.ware.dao.WareSkuDao;
import com.gmy.gulimall.ware.entity.WareSkuEntity;
import com.gmy.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        final LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

        final String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            wrapper.eq(WareSkuEntity::getSkuId, skuId);
        }
        final String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            wrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {

        List<SkuHasStockVo> data = skuIds.stream()
                .map(skuId -> {
                    SkuHasStockVo vo = new SkuHasStockVo();

                    // 查询当前 sku 库存的总量
                    Long count = this.baseMapper.getSkuStock(skuId);
                    vo.setSkuId(skuId);
                    vo.setStock(count != null);
                    return vo;
                })
                .collect(Collectors.toList());
        return data;
    }

    /**
     * 默认 运行时异常都会 回滚的
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = NoStockException.class)
    public boolean lockCount(WareSkuLockVo vo) {

        // 1. 找到 每个商品 在哪里都有库存
        List<OrderConfirmVo.OrderItemVO> locks = vo.getLocks();
        List<SkuWareHasStock> whereHasStock = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setNum(item.getCount());
            stock.setSkuId(skuId);
            // 查询这个商品哪些仓库 有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        Boolean flag = true;

        // 2.锁定库存
        for (SkuWareHasStock ware : whereHasStock) {
            boolean skuStocked = false;
            Long skuId = ware.getSkuId();
            List<Long> wareIds = ware.getWareId();
            if (CollectionUtils.isNotEmpty(wareIds)) {
                for (Long wareId : wareIds) {

                    Long count = wareSkuDao.lockSkuStock(skuId, wareId, ware.getNum());
                    if (count == 1) {
                        // 成功
                        skuStocked = true;
                        break;
                    }

                }
                if (!skuStocked) {
                    // 所有的仓库 都 没锁住
                    throw new NoStockException(skuId);
                }

            } else {
                throw new NoStockException(skuId);
            }
        }

        return true;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;

        private Integer num;
        private List<Long> wareId;
    }

}