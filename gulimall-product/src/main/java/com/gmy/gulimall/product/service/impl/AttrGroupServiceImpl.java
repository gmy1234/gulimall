package com.gmy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.product.entity.AttrEntity;
import com.gmy.gulimall.product.service.AttrService;
import com.gmy.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
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

import com.gmy.gulimall.product.dao.AttrGroupDao;
import com.gmy.gulimall.product.entity.AttrGroupEntity;
import com.gmy.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        final String key = (String) params.get("key");
        // select * from pms_attr_group  where catelog_id = ?
        // and (attr_group_id = key or attr_group_name = key)
        final LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        // 模糊查询 ；不为空
        if (StringUtils.hasText(key)){
            wrapper.and( obj ->{
                obj.eq(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key);
            });
        }
        if (categoryId == 0) {
            final IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }else {
            // 三级分类Id
            wrapper.eq(AttrGroupEntity::getCatelogId, categoryId);
            final IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }

    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1、查询所有分组信息
        final LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        final List<AttrGroupEntity> groupsInfo = this.baseMapper.selectList(wrapper);

        // 2、查询分组下的属性信息
        final List<AttrGroupWithAttrsVo> res = groupsInfo.stream().map(group -> {
            final AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, vo);
            // 根据分组Id ，找出关联的所有属性
            final List<AttrEntity> attrs = attrService.getRelationAttr(vo.getAttrGroupId());
            vo.setAttrs(attrs);
            return vo;
        }).collect(Collectors.toList());

        return res;
    }

}