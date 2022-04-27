package com.gmy.guliorder.order.web;

import com.gmy.guliorder.order.service.OrderService;
import com.gmy.guliorder.order.vo.OrderConfirmVo;
import com.gmy.guliorder.order.vo.OrderSubmitResponseVO;
import com.gmy.guliorder.order.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;


@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirm", confirmVo);
        return "confirm";
    }

    @GetMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO vo) {

        OrderSubmitResponseVO res = orderService.submitOrder(vo);
        if (res.getCode() == 0) {
            return "pay";
        }

        return "redirect:http://order.gulimall.com/toTrade";
    }

}
