package com.gmy.gulimall.ware.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    /**
     *  序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange exchange(){
        return new TopicExchange("stock-event-exchange", true, false);
    }


    @Bean
    public Queue queue(){
        return new Queue("stock.release.stock.queue", true, false, false,null);
    }

    @Bean
    public Queue delayQueue(){
        Map<String, Object > args = new HashMap<>();
        //  死信路由
        args.put("x-dead-letter-exchange", "stock-event-exchange");
        // 延时队列往死信路由那扔消息用的路由键
        args.put("x-dead-letter-routing-key", "stock.release");
        // 消息存活时间，1分钟
        args.put("x-message-ttl", 90000L);

        return new Queue("stock.delay.queue", true, false, false, args);
    }

    @Bean
    public Binding stockReleaseBinding(){

        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#", null);
    }


    /**
     * 交换机和普通队列的绑定关系
     * 路由key ： order.release.order
     * @return 绑定键
     */
    @Bean
    public Binding stockLockedBinding(){

        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "stock.lock.#",
                null);
    }
}


