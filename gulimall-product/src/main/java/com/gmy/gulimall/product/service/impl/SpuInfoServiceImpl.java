package com.gmy.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.gmy.common.ProductConstant;
import com.gmy.common.to.SkuReductionTo;
import com.gmy.common.to.SpuBoundTo;
import com.gmy.common.utils.R;
import com.gmy.gulimall.product.dao.SpuInfoDescDao;
import com.gmy.gulimall.product.entity.*;
import com.gmy.gulimall.product.feign.CouponFeignService;
import com.gmy.gulimall.product.service.*;
import com.gmy.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService descService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;


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
        BeanUtils.copyProperties(skuInfoVo, spuBasicInfo);
        spuBasicInfo.setCreateTime(new Date());
        spuBasicInfo.setUpdateTime(new Date());
        this.baseMapper.insert(spuBasicInfo);

        // 2.保存保存 spu 的描述图片 pms_spu_info_desc
        final List<String> decript = skuInfoVo.getDecript();
        final SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuBasicInfo.getId());
        descEntity.setDecript(String.join(",", decript));
        descService.save(descEntity);

        // 3.保存 spu 的图片集合 pms_spu_images
        final List<String> images = skuInfoVo.getImages();
        // 图片不为空 才保存
        if (CollectionUtils.isNotEmpty(images)) {
            final List<SpuImagesEntity> collect = images.stream()
                    .map(item -> {
                        final SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                        spuImagesEntity.setSpuId(spuBasicInfo.getId());
                        spuImagesEntity.setImgUrl(item);
                        return spuImagesEntity;
                    }).collect(Collectors.toList());
            imagesService.saveBatch(collect);
        }

        // 4.保存规格参数， pms_product_attr_value
        final List<BaseAttrs> baseAttrs = skuInfoVo.getBaseAttrs();
        final List<ProductAttrValueEntity> collect = baseAttrs.stream()
                .map(item -> {
                    final ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
                    productAttrValue.setAttrId(item.getAttrId());
                    final AttrEntity attr = attrService.getById(item.getAttrId());
                    productAttrValue.setAttrName(attr.getAttrName());
                    productAttrValue.setAttrValue(item.getAttrValues());
                    productAttrValue.setQuickShow(item.getShowDesc());
                    productAttrValue.setSpuId(spuBasicInfo.getId());
                    return productAttrValue;
                }).collect(Collectors.toList());
        attrValueService.saveBatch(collect);


        // 4.5 保存 spu 的积分信息 sms_spu_bounds 调用远程服务

        final SpuBoundTo spuBoundTo = new SpuBoundTo();
        final Bounds bounds = skuInfoVo.getBounds();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuBasicInfo.getId());
        final R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0){
            log.error("远程保存积分信息失败");
        }

        // 5.保存 当前 spu 对应的所有 sku 信息
        final List<Skus> skuInfos = skuInfoVo.getSkus();
        if (CollectionUtils.isNotEmpty(skuInfos)) {
            skuInfos.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }

                // 5.1 sku 的基本信息 pms_sku_info
                final SkuInfoEntity skuInfo = new SkuInfoEntity();
//                skuInfos 里边有的
//                skuName;   price;
//                skuTitle;  skuSubtitle;
                BeanUtils.copyProperties(item, skuInfo);
                skuInfo.setBrandId(spuBasicInfo.getBrandId());
                skuInfo.setCatalogId(spuBasicInfo.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSpuId(spuBasicInfo.getId());
                skuInfo.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfo);
                final Long skuId = skuInfo.getSkuId();

                // 5.2 sku 的图片信息 pms_sku_images
                final List<SkuImagesEntity> skuImages = item.getImages().stream()
                        .map(img -> {
                            final SkuImagesEntity skuImage = new SkuImagesEntity();
                            skuImage.setSkuId(skuId);
                            skuImage.setImgUrl(img.getImgUrl());
                            skuImage.setDefaultImg(img.getDefaultImg());
                            return skuImage;
                        }).filter(entity -> StringUtils.isNotBlank(entity.getImgUrl()))
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImages);

                // 5.3 保存sku 的销售属性 信息 pms_sku_sale_attr_value
                final List<Attr> attr = item.getAttr();
                final List<SkuSaleAttrValueEntity> saleAttrValue = attr.stream().map(a -> {
                    final SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuId);
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrValue);

                // 5.4 sku 的优惠信息，满减信息，gulimall_sms ——>数据库的表
                final SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                final R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode() != 0) {
                    log.error("远程保存sku积分信息失败");
                }
            });
        }
    }


    @Override
    public void saveBasicInfo(SpuInfoEntity spuBasicInfo) {
        this.baseMapper.insert(spuBasicInfo);
    }


}