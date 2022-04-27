package com.gmy.gulimall.cart.controller;

import com.gmy.gulimall.cart.service.CartService;
import com.gmy.gulimall.cart.vo.CartItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentUserCartItems(){

        return  cartService.getUserCartItems();
    }


    /**
     * 购物车页面的请求跳转
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String getCartListPage() throws ExecutionException, InterruptedException {

        //快速得到用户信息：id,user-key
        // UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();

        cartService.getCart();

        return "cartList";
    }



    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes model) {

        try {
            cartService.addToCart(skuId, num);
            model.addAttribute("skuId", skuId);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }

    /**
     * 跳转到添加购物车成功页面
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo.toString());
        return "success";
    }

    /**
     * 获取当前用户的购物车商品项
     *
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {

        return cartService.getUserCartItems();
    }

}
