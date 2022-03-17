package com.gmy.gulimall.member.dao;

import com.gmy.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
