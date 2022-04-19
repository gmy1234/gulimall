package com.gmy.gulimall.auth.feign;

import com.gmy.common.utils.R;
import com.gmy.gulimall.auth.vo.MemberLoginVo;
import com.gmy.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R Register(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    R Login(@RequestBody MemberLoginVo vo);

}
