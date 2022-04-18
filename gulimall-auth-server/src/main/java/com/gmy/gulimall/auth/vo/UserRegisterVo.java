package com.gmy.gulimall.auth.vo;

import lombok.Data;



import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户们必须提交")
    private String userName;

    @NotEmpty(message = "密码必须填写")
    private String password;

//    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    @NotEmpty(message = "手机号必须填写")
    private String phone;

    @NotEmpty(message = "验证码必须填写")
    private String code;
}
