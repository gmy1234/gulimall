package com.gmy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.ProductAttrValueDao;
import com.gmy.gulimall.product.entity.ProductAttrValueEntity;
import com.gmy.gulimall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        final LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);

        return this.baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        // 删除所有信息，再插入
        final LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
        this.baseMapper.delete(wrapper);

        // 插入：
        final List<ProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

}