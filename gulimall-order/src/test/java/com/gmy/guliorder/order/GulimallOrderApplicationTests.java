package com.gmy.guliorder.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gmy.guliorder.order.entity.OrderEntity;
import com.gmy.guliorder.order.entity.OrderReturnReasonEntity;
import com.gmy.guliorder.order.service.OrderReturnReasonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    OrderReturnReasonService orderReturnReasonService;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

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

    @Test
        /* 创建交换机 */
    void createExchange(){
        DirectExchange directExchange = new DirectExchange(
                "my-exchange", true, false);

        amqpAdmin.declareExchange(directExchange);
        log.info("交换机创建OK,名字为：", directExchange.getName());
    }

    @Test
        /* 创建队列 */
    void createQueue(){
        String s = amqpAdmin.declareQueue(
                new Queue("my-queue", true, false, false));
        System.out.println(s);
        log.info("队列创建OK");
    }

    @Test
    void Bind(){

        // String destination,目的地  DestinationType destinationType 目的地类型,
        // String exchange,交换机  String routingKey, 路由key
        // @Nullable Map<String, Object> arguments
        // 解释：将 交换机和目的地进行绑定，使用 routingKey 作为路由键
        amqpAdmin.declareBinding(
                new Binding("my-queue", Binding.DestinationType.QUEUE,
                        "my-exchange", "my.java",null));
        log.info("绑定成功");
    }

    @Test
    void rabbitTemplate(){
        // 发送的消息如果是对象，我们会使用序列化机制，将对象写出去，对象必须实现 serializable
        // 发送消息可以是对象 JSON 类型
        rabbitTemplate.convertAndSend("my-exchange",
                "my.java", new OrderEntity());

        log.info("消息发送完成");
    }

}
