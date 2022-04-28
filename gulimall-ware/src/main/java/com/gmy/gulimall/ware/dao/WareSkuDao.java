package com.gmy.gulimall.ware.dao;

import com.gmy.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:48:48
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Long getSkuStock(Long skuId);

    /**
     * 查询 skuID对应的仓库哪个仓库有 库存
     * @param skuId
     * @return
     */
    List<Long> listWareIdHasStock(@Param("skuId") Long skuId);

    /**
     * 锁库存
     * @param skuId
     * @param wareId
     * @param num
     */
    Long lockSkuStock(@Param("skuId")Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
