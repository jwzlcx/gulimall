package com.atguigu.gulimall.product;

import com.alibaba.fastjson.JSON;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Test
    public void redisTest( ) {
        ValueOperations<String, String> op = stringRedisTemplate.opsForValue();
        op.set("redis", UUID.randomUUID().toString());

        System.out.println(op.get("redis"));
    }
    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }



}
