package com.gmy.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.gmy.common.exception.BizCodeEnume;
import com.gmy.gulimall.member.feign.CouponFeignService;
import com.gmy.gulimall.member.vo.MemberLoginVo;
import com.gmy.gulimall.member.vo.MemberRegisterVo;
import com.gmy.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gmy.gulimall.member.entity.MemberEntity;
import com.gmy.gulimall.member.service.MemberService;
import com.gmy.common.utils.PageUtils;
import com.gmy.common.utils.R;



/**
 * 会员
 *
 * @author gmy
 * @email guanmengyang528@gmail.com
 * @date 2022-03-17 11:33:13
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    CouponFeignService couponFeignService;

    @PostMapping("/login")
    public R Login(@RequestBody MemberLoginVo vo){

        System.out.println("远程调用login服务了");
        MemberEntity entity = memberService.login(vo);
        if (entity != null) {
            return R.ok();

        }
        return R.error(BizCodeEnume.ACCOUNT_PASSWORD_EXCEPTION.getCode(),
                BizCodeEnume.ACCOUNT_PASSWORD_EXCEPTION.getMsg());
    }



    @PostMapping("/register")
    public R Register(@RequestBody MemberRegisterVo vo){
        System.out.println("远程调用注册服务了");
        memberService.register(vo);
        return R.ok();
    }

    @PostMapping(value = "/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {

        MemberEntity memberEntity = memberService.login(socialUser);

        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    @PostMapping(value = "/weixin/login")
    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {

//        MemberEntity memberEntity = memberService.login(accessTokenInfo);
//        if (memberEntity != null) {
//            return R.ok().setData(memberEntity);
//        } else {
//            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
//        }
        return R.ok();
    }


    @RequestMapping("/coupons")
    public R test(){
        final MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        final R memberCoupons = couponFeignService.memberCoupons();
        final Object coupons = memberCoupons.get("coupons");
        //                 用户                       优惠卷
        return R.ok().put("member", memberEntity).put("coupons", coupons);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
