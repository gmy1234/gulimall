package com.gmy.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author gmyDL
 */
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.gmy.gulimall.ware.dao")
@EnableRabbit
@EnableDiscoveryClient
public class GulimallWareApplication {

    public static void main(String[] args) {
        // mactest
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
