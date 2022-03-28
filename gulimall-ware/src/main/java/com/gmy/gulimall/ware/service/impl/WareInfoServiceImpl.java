package com.gmy.gulimall.ware.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.ware.dao.WareInfoDao;
import com.gmy.gulimall.ware.entity.WareInfoEntity;
import com.gmy.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        final LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();

        final String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)){
            wrapper.eq(WareInfoEntity::getId, key).or()
                    .like(WareInfoEntity::getName, key).or()
                    .like(WareInfoEntity::getAddress, key).or()
                    .like(WareInfoEntity::getAreacode, key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}