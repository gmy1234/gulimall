package com.gmy.guliorder.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gmy.guliorder.order.entity.OrderReturnReasonEntity;
import com.gmy.guliorder.order.service.OrderReturnReasonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    OrderReturnReasonService orderReturnReasonService;

    @Test
    void contextLoads() {
//        final OrderReturnReasonEntity one = new OrderReturnReasonEntity();
//        one.setName("iii");
//        one.setSort(2);
//        one.setStatus(0);
//        orderReturnReasonService.save(one);
//        System.out.println("OK");
        final List<OrderReturnReasonEntity> list = orderReturnReasonService.list(
                new QueryWrapper<OrderReturnReasonEntity>().eq("name", "iii"));
        list.forEach(System.out::println);
    }

}
