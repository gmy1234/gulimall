package com.gmy.guliorder.order.config;


import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 定制 RabbitTemplate
    @PostConstruct
    public void initRabbitTemplate(){
        // 1、设置消息确认回掉
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达服务器：Broke代理， ack 就 = true
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

        // 2、设置消息抵达队列的确认回掉
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {

            /**
             * 触发时机：消息没有投递给指定队列，就会触发这个失败回掉
             * @param returned  ReturnedMessage类
             *   Message message： 投递失败的消息的详细信息
             *
             * 	 replyCode; 回复的状态码
             *
             * 	 replyText; 回复的文本内容
             *
             * 	 exchange;  交换机
             *
             * 	routingKey; 这个消息用的路由key
             */
            @Override
            public void returnedMessage(ReturnedMessage returned) {
                System.out.println("message: ->" + returned.getMessage());
                System.out.println("ReplyCode: ->" + returned.getReplyCode());
                System.out.println("ReplyText: ->" + returned.getReplyText());
                System.out.println("Exchange: ->" + returned.getExchange());
                System.out.println("RoutingKey: ->" + returned.getRoutingKey());
            }
        });

        // 3、消费端确认，保证每一个消息被正确消费，此时 broker 才可以删除消息


    }

}
