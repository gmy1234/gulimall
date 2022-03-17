package com.gmy.gulimall.member.dao;

import com.gmy.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
