package com.gmy.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.product.entity.SkuInfoEntity;
import com.gmy.gulimall.product.vo.SpuSaveVo;

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


    /**
     * 大保存
     * @param skuInfoVo 保存的数据
     */
    void saveSpuInfo(SpuSaveVo skuInfoVo);
}

