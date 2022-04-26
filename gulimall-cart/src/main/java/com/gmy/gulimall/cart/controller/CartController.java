package com.gmy.gulimall.cart.controller;

import com.gmy.common.constant.AuthServerConstant;
import com.gmy.gulimall.cart.service.CartService;
import com.gmy.gulimall.cart.vo.CartItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    CartService cartService;

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
            return "cartList";
        }
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId")Long skuId,
                            @RequestParam("num") Integer num,
                            Model model){

        try {
            CartItemVo cartItemVo = cartService.addToCart(skuId, num);

            model.addAttribute("item", cartItemVo);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return "success";
    }
}
