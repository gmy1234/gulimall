package com.gmy.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.common.utils.HttpUtils;
import com.gmy.gulimall.member.entity.MemberLevelEntity;
import com.gmy.gulimall.member.service.MemberLevelService;
import com.gmy.gulimall.member.vo.MemberLoginVo;
import com.gmy.gulimall.member.vo.MemberRegisterVo;
import com.gmy.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.Query;

import com.gmy.gulimall.member.dao.MemberDao;
import com.gmy.gulimall.member.entity.MemberEntity;
import com.gmy.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {

        MemberEntity memberEntity = new MemberEntity();
        // 1.检查用户名是否唯一
        boolean name = this.checkUserName(vo.getUserName());
        // 2。检查手机号是否唯一
        boolean phone = this.checkPhone(vo.getPhone());

        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());

        // 密码加密存储；
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);

        // 设置默认等级
        MemberLevelEntity defaultLevel = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(defaultLevel.getId());
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public boolean checkPhone(String phone) {
        return false;
    }

    @Override
    public boolean checkUserName(String username) {
        return false;
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {

        String loginAcct = vo.getLoginAcct();
        // 页面传来的密码
        String password = vo.getPassword();

        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getUsername, loginAcct)
                .or()
                .eq(MemberEntity::getMobile, loginAcct);
        MemberEntity user = this.baseMapper.selectOne(wrapper);

        if (user != null) {
            // 数据库里来的密码
            String passwordFromDB = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 密码是否匹配？
            boolean matches = passwordEncoder.matches(password, passwordFromDB);
            if (matches) {
                return user;
            }
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception{

        System.out.println("远程调用了Member服务----");
        // 登陆和注册合并逻辑
        String uid = socialUser.getUid();

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = baseMapper.selectOne(
                new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        System.out.println("memberEntity-：" + memberEntity);

        if (memberEntity != null) {
            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String, String> query = new HashMap<>();
            query.put("access_token", socialUser.getAccess_token());
            query.put("uid", socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                    "/2/users/show.json", "get", new HashMap<>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender) ? 1 : 0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                baseMapper.insert(register);
            }
            return register;
        }


    }

}