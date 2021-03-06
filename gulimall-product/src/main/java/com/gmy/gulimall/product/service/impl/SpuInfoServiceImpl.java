package com.gmy.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.common.constant.ProductConstant;
import com.gmy.common.to.SkuHasStockVo;
import com.gmy.common.to.SkuReductionTo;
import com.gmy.common.to.SpuBoundTo;
import com.gmy.common.to.es.SkuESModule;
import com.gmy.common.utils.R;
import com.gmy.gulimall.product.entity.*;
import com.gmy.gulimall.product.feign.CouponFeignService;
import com.gmy.gulimall.product.feign.SearchFeignService;
import com.gmy.gulimall.product.feign.WareFeignService;
import com.gmy.gulimall.product.service.*;
import com.gmy.gulimall.product.vo.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;


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
        // 1.???????????????????????????  pms_spu_info
        final SpuInfoEntity spuBasicInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(skuInfoVo, spuBasicInfo);
        spuBasicInfo.setCreateTime(new Date());
        spuBasicInfo.setUpdateTime(new Date());
        this.baseMapper.insert(spuBasicInfo);

        // 2.???????????? spu ??????????????? pms_spu_info_desc
        final List<String> decript = skuInfoVo.getDecript();
        final SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuBasicInfo.getId());
        descEntity.setDecript(String.join(",", decript));
        descService.save(descEntity);

        // 3.?????? spu ??????????????? pms_spu_images
        final List<String> images = skuInfoVo.getImages();
        // ??????????????? ?????????
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

        // 4.????????????????????? pms_product_attr_value
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


        // 4.5 ?????? spu ??????????????? sms_spu_bounds ??????????????????

        final SpuBoundTo spuBoundTo = new SpuBoundTo();
        final Bounds bounds = skuInfoVo.getBounds();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuBasicInfo.getId());
        final R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("??????????????????????????????");
        }

        // 5.?????? ?????? spu ??????????????? sku ??????
        final List<Skus> skuInfos = skuInfoVo.getSkus();
        if (CollectionUtils.isNotEmpty(skuInfos)) {
            skuInfos.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }

                // 5.1 sku ??????????????? pms_sku_info
                final SkuInfoEntity skuInfo = new SkuInfoEntity();
//                skuInfos ????????????
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

                // 5.2 sku ??????????????? pms_sku_images
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

                // 5.3 ??????sku ??????????????? ?????? pms_sku_sale_attr_value
                final List<Attr> attr = item.getAttr();
                final List<SkuSaleAttrValueEntity> saleAttrValue = attr.stream().map(a -> {
                    final SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuId);
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(saleAttrValue);

                // 5.4 sku ?????????????????????????????????gulimall_sms ??????>???????????????
                final SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                final R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode() != 0) {
                    log.error("????????????sku??????????????????");
                }
            });
        }
    }


    @Override
    public void saveBasicInfo(SpuInfoEntity spuBasicInfo) {
        this.baseMapper.insert(spuBasicInfo);
    }

    @Override
    public PageUtils queryPageMyCondition(Map<String, Object> params) {
        final LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();

        /**
         * status:
         * key:
         * brandId: 0
         * catelogId: 0
         */
        final String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> {
                w.eq(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key);
            });
        }
        final String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        final String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equals(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        final String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && ! "0".equals(brandId) ) {
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // ?????? spuID ????????? ?????????SKU??????
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> spuIds = skus.stream().map(SkuInfoEntity::getSpuId).collect(Collectors.toList());

        // TODO: 4?????????sku???????????????????????????
        List<ProductAttrValueEntity> baseAttr = attrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttr.stream().map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());

       List<Long> searchAttrIds =  attrService.searchAttrs(attrIds);

       // sku ??? ?????? ?????????
        HashSet<Long> zj = new HashSet<>(searchAttrIds);
        List<SkuESModule.Attrs> attrsList = baseAttr.stream()
                .filter(item -> zj.contains(item.getAttrId()))
                .map(item -> {
                    SkuESModule.Attrs attrs = new SkuESModule.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                })
                .collect(Collectors.toList());

        // TODO: 1???????????????????????????????????????????????????
        Map<Long, Boolean> hasStock = null;
        try {
            R r = wareFeignService.getSkuHasStock(spuIds);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            List<SkuHasStockVo> data =  r.getData(typeReference);

            hasStock = data.stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getStock));

        }catch (Exception e){
            log.error("????????????????????????????????????????????????", e);
        }


        // 2???????????????SKU??????
        Map<Long, Boolean> finalHasStock = hasStock;
        List<SkuESModule> upProduct = skus.stream().map(sku -> {

            SkuESModule skuESModule = new SkuESModule();
            BeanUtils.copyProperties(sku, skuESModule);
            // img , price
            skuESModule.setSkuPrice(sku.getPrice());
            skuESModule.setSkuImg(sku.getSkuDefaultImg());

            // TODO: 2??????????????????hotScore ??????0
            // ?????????hasStock
            if (finalHasStock == null) {
                skuESModule.setHasStock(true);
            }else {
                skuESModule.setHasStock(finalHasStock.get(sku.getSkuId()));

            }
            skuESModule.setHotScore(0L);
            // TODO: 3?????????????????????????????????
            BrandEntity brand = brandService.getById(skuESModule.getBrandId());
            skuESModule.setBrandName(brand.getName());
            skuESModule.setBrandImg(brand.getLogo());
            // ??????????????????
            CategoryEntity cate = categoryService.getById(skuESModule.getCatalogId());
            skuESModule.setCatalogName(cate.getName());

            // ??????????????????
            skuESModule.setAttrs(attrsList);


            return skuESModule;
        }).collect(Collectors.toList());

        // TODO: 5???????????????es ???????????????gulimall-search
        R r = searchFeignService.productStatusUp(upProduct);
        if (r.getCode() == 0) {
            // ??????
            // todo 6: ?????? spu ???????????????
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.UP_SPU.getCode());
            this.baseMapper.updateById(spuInfoEntity);
        }else {
            // ??????
            // TODO: ????????????????????????????????? ????????????
            // feign ????????????
            /**
             *  ??????????????????????????????????????? json
             *  ??????????????????????????????????????????????????????????????????
             *  3???????????????????????????????????????????????????
             *      while???true??????
             *      try{
             *
             *      }
             */
        }

    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        SkuInfoEntity sku = skuInfoService.getById(skuId);
        return this.getById(sku.getSpuId());
    }


}