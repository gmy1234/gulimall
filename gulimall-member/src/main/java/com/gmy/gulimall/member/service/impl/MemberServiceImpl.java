package com.gmy.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gmy.gulimall.member.entity.MemberLevelEntity;
import com.gmy.gulimall.member.service.MemberLevelService;
import com.gmy.gulimall.member.vo.MemberLoginVo;
import com.gmy.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

}