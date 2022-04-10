package com.gmy.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedisConfig {

    /**
     * 所有对 Redisson 的使用都是通过 RedissonClient 对象
     *
     * @return 配置
     * @throws IOException 无
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        // 1、创建配置
        Config config = new Config();
        // Redis url should start with redis:// or redis://
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        // 2、根据 Config 创建出 RedissonClient 实例
        return Redisson.create(config);
    }
}
