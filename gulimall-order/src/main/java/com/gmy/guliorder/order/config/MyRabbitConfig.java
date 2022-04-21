package com.gmy.guliorder.order.config;


import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     *  使 json 序列化机制，进行消息转换
     * @return json 类型的转换器
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 定制 RabbitTemplate
    @PostConstruct
    public void initRabbitTemplate(){
        // 设置确认回掉
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData  消息的关联数据（消息的唯一 ID）
             * @param ack 消息是否成功收到
             * @param cause 消息接受失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("CorrelationData 是 ：->>>" +correlationData);
                System.out.println("ack 是 ：->>>" +ack);
                System.out.println("cause 是 ：->>>" +cause);
            }
        });

    }
}
