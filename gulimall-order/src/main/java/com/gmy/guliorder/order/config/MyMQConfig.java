package com.gmy.guliorder.order.config;

import com.gmy.guliorder.order.entity.OrderEntity;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {


    // 监听队列：
    @RabbitListener(queues = {"order.release.order.queue"})
    public void listener(OrderEntity order){
        System.out.println("收到过期的订单，准备关闭订单" + order);

    }



    /**
     * 死信队列
     * 容器中 的组件 @bean 注解 会自动创建队列交换机和绑定件（RabbitMQ 里面没有）
     * 一旦创建好，属性再次发生变化，也不会覆盖。
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){

        Map<String, Object > args = new HashMap<>();
        //  死信路由
        args.put("x-dead-letter-exchange", "order-event-exchange");
        // 延时队列往死信路由那扔消息用的路由键
        args.put("x-dead-letter-routing-key", "order.release.order");
        // 消息存活时间，1分钟
        args.put("x-message-ttl", 60000L);

        return  new Queue("order.delay.queue", true, false, false, args);
    }


    /**
     * 普通的队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue(){

        return  new Queue("order.release.order.queue", true, false, false);
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){

        return new TopicExchange("order-event-exchange", true, false);
    }

    /**
     * 交换机 和 死信队列绑定关系
     * @return 交换机 和 死信队列 绑定key
     */
    @Bean
    public Binding orderCreateOrderBinding(){

        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order", null);
    }


    /**
     * 交换机和普通队列的绑定关系
     * 路由key ： order.release.order
     * @return 绑定键
     */
    @Bean
    public Binding orderReleaseOrder(){

        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

}
