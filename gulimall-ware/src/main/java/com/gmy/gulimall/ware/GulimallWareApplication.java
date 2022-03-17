package com.gmy.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author gmyDL
 */
@SpringBootApplication
@MapperScan("com.gmy.gulimall.ware.dao")
public class GulimallWareApplication {

    public static void main(String[] args) {
        // mactest
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
