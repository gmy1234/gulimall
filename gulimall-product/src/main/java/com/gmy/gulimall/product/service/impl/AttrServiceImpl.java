package com.gmy.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.common.constant.ProductConstant;
import com.gmy.common.exception.RRException;
import com.gmy.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.gmy.gulimall.product.dao.AttrGroupDao;
import com.gmy.gulimall.product.dao.CategoryDao;
import com.gmy.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.gmy.gulimall.product.entity.AttrGroupEntity;
import com.gmy.gulimall.product.entity.CategoryEntity;
import com.gmy.gulimall.product.service.CategoryService;
import com.gmy.gulimall.product.vo.AttrGroupRelationVo;
import com.gmy.gulimall.product.vo.AttrRespVo;
import com.gmy.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

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
        // 1.??????????????????
        this.save(attrEntity);
        // 2.??????????????????
        // ????????????????????????????????????
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                &&
                attr.getAttrGroupId() != null) {
            final AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    /**
     * ????????????????????????
     * @param params ????????????
     * @param categoryId ??????Id
     * @param type ???????????? ?????? ????????????
     * @return ??????
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId, String type) {
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 0??????????????????1???????????????
        queryWrapper.eq(AttrEntity::getAttrType,
                "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :  ProductConstant.AttrEnum.ATTR_ENUM_SALE.getCode());
        if (categoryId != 0){
            queryWrapper.eq(AttrEntity::getCatelogId, categoryId);
        }
        // ???????????????
        final String key = (String) params.get("key");
        if (StringUtils.hasText(key)){
            // ??????????????? ?????????Id ????????????
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

        // ?????? ??????????????? ????????????
        final List<AttrEntity> records = page.getRecords();
        final List<AttrRespVo> response = records.stream()
                .map(attrEntity -> {
                    final AttrRespVo attrRespVo = new AttrRespVo();
                    // ??? AttrEntity ??????????????? ????????? respV???
                    BeanUtils.copyProperties(attrEntity, attrRespVo);
                    // ????????????????????????
                    // ???????????????????????????
                    if ("base".equalsIgnoreCase(type)){
                        final AttrAttrgroupRelationEntity arrtId = relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
                        if (arrtId != null && arrtId.getAttrGroupId()!=null) {
                            final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(arrtId.getAttrGroupId());
                            attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                        }
                    }

                    // ?????????????????????
                    final CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
                    if (categoryEntity != null) {
                        attrRespVo.setCatelogName(categoryEntity.getName());
                    }
                    return attrRespVo;
                }).collect(Collectors.toList());

        pageUtils.setList(response);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {

        AttrRespVo respVo = new AttrRespVo();
        final AttrEntity attrEntity = this.baseMapper.selectById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 1.??????????????????
            final LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
            final AttrAttrgroupRelationEntity relation = relationDao.selectOne(wrapper);
            if (relation != null) {
                final Long attrGroupId = relation.getAttrGroupId();
                respVo.setAttrGroupId(attrGroupId);
                final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 2.??????????????????
        final Long catelogId = attrEntity.getCatelogId();

        final Long[] catelogPath = categoryService.findCatalogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        final CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }
        return respVo;
    }

    @Override
    @Transactional
    public void updateAttrVo(AttrVo attrVo) {
        final AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.baseMapper.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){

            // 1.?????????????????????
            final LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrId, attrVo.getAttrId());
            final AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());

            final Long aLong = relationDao.selectCount(wrapper);
            // ????????????
            if (aLong > 0){
                relationDao.update(relationEntity, wrapper);
            }else {
                // ????????????
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * ????????????Id ????????????????????????????????????
     * @param attrgroupId ??????Id
     * @return ??????
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrEntity> res = new ArrayList<>();

        final LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);
        final List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(entities)){
            final List<Long> attrEntites = entities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId)
                    .collect(Collectors.toList());

            res = this.listByIds(attrEntites);
        }
        return res;
    }

    @Override
    public void deleteRelationAttr(AttrGroupRelationVo[] vos) {
        // ???AttrGroupRelationVo ?????? ????????? AttrAttrgroupRelationEntity ??????
        final List<AttrAttrgroupRelationEntity> entities = Arrays.stream(vos)
                .map(item -> {
                    final AttrAttrgroupRelationEntity attrRelation = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(item, attrRelation);
                    return attrRelation;
                }).collect(Collectors.toList());
        relationDao.deletedBatch(entities);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1??????????????? ????????????????????????????????? ?????????????????????
        final AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        final Long catelogId = attrGroupEntity.getCatelogId();

        // 2????????????????????????????????????????????????????????????
        // 2.1 ???????????????????????????????????????
        final LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        final List<AttrGroupEntity> groups = attrGroupDao.selectList(wrapper);
        final LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper1 = new LambdaQueryWrapper<>();
        final List<Long> collect = groups.stream()
                .map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        // 2.2 ???????????????????????????
        wrapper1.in(AttrAttrgroupRelationEntity::getAttrGroupId, collect);
        final List<AttrAttrgroupRelationEntity> groupId = relationDao.selectList(wrapper1);
        final List<Long> attrIds = groupId.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        // 2.3??? ??????
        final QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (CollectionUtils.isNotEmpty(attrIds)){
            queryWrapper.notIn("attr_id", attrIds);
        }

        final String key = (String) params.get("key");
        if (StringUtils.hasText(key)){
            queryWrapper.and( (w) ->{
                w.eq("attr_id", key).or().eq("attr_name", key);
            });
        }

        final IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> searchAttrs(List<Long> attrIds) {

        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();

        if (CollectionUtils.isNotEmpty(attrIds)){
            wrapper.in(AttrEntity::getAttrId, attrIds)
                    .and( r -> r.eq(AttrEntity::getSearchType,1));

            List<AttrEntity> attrEntities = this.baseMapper.selectList(wrapper);

            return  attrEntities.stream().map(AttrEntity::getAttrId
            ).collect(Collectors.toList());
        }
        throw new RRException("????????????");
    }


}