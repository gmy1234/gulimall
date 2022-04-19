package com.gmy.gulimall.auth.controller;


import com.gmy.common.utils.R;
import com.gmy.gulimall.auth.feign.MemberFeignService;
import com.gmy.gulimall.auth.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    MemberFeignService memberFeignService;

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes){

        System.out.println("调用注册接口了");

        if (bindingResult.hasErrors()) {
            // 校验错误到信息
            Map<String, String> errorInfo = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            DefaultMessageSourceResolvable::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors", errorInfo);
            // 校验出错，转发到注册页面
            /* TODO: 重定向携带数据，利用session原理，将数据放在session中，只要跳到下一个页面取出这个数据后，
                Session数据就会删掉
             */
            return "redirect:http://auth.gulimall.com/register.html";
        }

        //真正的注册，远程调用服务
        // TODO：验证码未校验
        R register = memberFeignService.Register(vo);
        if (register.getCode() == 0) {
            // 注册成功了
            return "redirect:http://auth.gulimall.com/login.html";
        }else{
            return "redirect:http://auth.gulimall.com/register.html";
        }

    }


}
