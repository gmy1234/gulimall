package com.gmy.gulimall.product.dao;

import com.gmy.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-16 22:46:40
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
