package com.gmy.gulimall.cart.controller;

import com.gmy.common.constant.AuthServerConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {

    /**
     * 购物车页面的请求跳转
     * @return
     */
    @GetMapping("/cart.html")
    public String getCartListPage(HttpSession session){

        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);

        if (attribute == null) {
            // 没登陆 去登陆页面
            return "redirect:http://auth.gulimall.com/login.html";
        }else {
            // 登陆了

        }

        return "cartList";
    }
}
