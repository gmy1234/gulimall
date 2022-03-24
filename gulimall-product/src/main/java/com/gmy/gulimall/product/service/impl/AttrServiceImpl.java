package com.gmy.gulimall.product.service.impl;

import com.alibaba.druid.sql.PagerUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.gmy.gulimall.product.dao.AttrGroupDao;
import com.gmy.gulimall.product.dao.CategoryDao;
import com.gmy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.gmy.gulimall.product.entity.AttrGroupEntity;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.AttrAttrgroupRelationService;
import com.gmy.gulimall.product.vo.AttrRespVo;
import com.gmy.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.product.dao.AttrDao;
import com.gmy.gulimall.product.entity.AttrEntity;
import com.gmy.gulimall.product.service.AttrService;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVo attr) {

        final AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 保存基本数据
        this.save(attrEntity);
        // 保存关联关系
        final AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (categoryId != 0){
            queryWrapper.eq(AttrEntity::getCatelogId, categoryId);
        }
        // 有检索条件
        final String key = (String) params.get("key");
        if (StringUtils.hasText(key)){
            // 模糊查询： 属性的Id 或者名字
            queryWrapper.and( (wrapper) ->{
                wrapper.eq(AttrEntity::getAttrId, key)
                        .or().like(AttrEntity::getAttrName, key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);

        // 查询 所属分类和 所属分组
        final List<AttrEntity> records = page.getRecords();
        final List<AttrRespVo> response = records.stream()
                .map(attrEntity -> {
                    final AttrRespVo attrRespVo = new AttrRespVo();
                    // 把 AttrEntity 基本信息， 拷贝到 respV里
                    BeanUtils.copyProperties(attrEntity, attrRespVo);
                    // 设置分组的名字：
                    // 通过属性分组关联表
                    final AttrAttrgroupRelationEntity arrtId = relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrEntity.getAttrId()));
                    if (arrtId != null) {
                        final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(arrtId.getAttrGroupId());
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }

                    // 设置分类的名字
                    final CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
                    if (categoryEntity != null) {
                        attrRespVo.setCatelogName(categoryEntity.getName());
                    }
                    return attrRespVo;
                }).collect(Collectors.toList());

        pageUtils.setList(response);
        return pageUtils;
    }

}