package com.gmy.gulimall.product;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;


@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    void stringRedisTest(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "word"+ UUID.randomUUID());

        String hello = ops.get("hello");
        System.out.println("之前保存的"  + hello);
    }

    @Test
    void contextLoads() {

    }

    @Test
    void redisson(){
        System.out.println(redissonClient);
    }

}
