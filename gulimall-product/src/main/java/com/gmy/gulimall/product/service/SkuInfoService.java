package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageMyCondition(Map<String, Object> params);

    /**
     *  通过spuID 查询 SKU的信息
     *
     * @param spuId spuID
     * @return 返回的集合
     */
    List<SkuInfoEntity> getSkusBySpuId(Long spuId);
}

