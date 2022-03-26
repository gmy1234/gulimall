package com.gmy.gulimall.product.service.impl;

import com.gmy.gulimall.product.vo.SpuSaveVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.SpuInfoDao;
import com.gmy.gulimall.product.entity.SpuInfoEntity;
import com.gmy.gulimall.product.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo skuInfoVo) {
        // 1.保存商品的基本信息  pms_spu_info
        final SpuInfoEntity spuBasicInfo = new SpuInfoEntity();
        this.saveBasicInfo(spuBasicInfo);

        // 2.保存保存 spu 的描述图片 pms_spu_info_desc

        // 3.保存 spu 的图片集合 pms_spu_images

        // 4.保存规格参数， pms_product_attr_value

        // 4.5 保存 spu 的积分信息 sms_spu_bounds

        // 5.保存 当前spu 对应的所有 sku 信息
        // 5.1 sku 的基本信息 pms_sku_info
        // 5.2 sku 的 图片信息 pms_sku_images
        // 5.3 保存sku 的销售属性 信息 pms_sku_sale_attr_value
        // 5.4 sku 的优惠信息，满减信息，gulimall_sms ——>数据库的表
        // 5.5


    }

    @Override
    public void saveBasicInfo(SpuInfoEntity spuBasicInfo) {
        this.baseMapper.insert(spuBasicInfo);
    }


}