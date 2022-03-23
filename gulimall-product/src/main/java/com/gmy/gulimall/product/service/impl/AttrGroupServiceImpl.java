package com.gmy.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import java.util.Map;
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
        if (categoryId == 0) {
            final IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>(null)
            );
            return new PageUtils(page);
        }else {
            final String key = (String) params.get("key");
            // select * from pms_attr_group  where catelog_id = ?
            // and (attr_group_id = key or attr_group_name = key)

            final LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
            // 三级分类Id
            wrapper.eq(AttrGroupEntity::getCatelogId, categoryId);

            if (StringUtils.hasText(key)){
                wrapper.and( obj ->{
                    obj.eq(AttrGroupEntity::getAttrGroupId, key)
                            .or()
                            .like(AttrGroupEntity::getAttrGroupName, key);
                });
            }

            final IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }

    }

}