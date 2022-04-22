package com.gmy.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gmy.common.utils.PageUtils;
import com.gmy.gulimall.member.entity.MemberEntity;
import com.gmy.gulimall.member.vo.MemberLoginVo;
import com.gmy.gulimall.member.vo.MemberRegisterVo;
import com.gmy.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);

    boolean checkPhone(String phone);

    boolean checkUserName(String username);

    /**
     * 用户登陆
     * @param vo 前段提交的数据
     * @return
     */
    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}

