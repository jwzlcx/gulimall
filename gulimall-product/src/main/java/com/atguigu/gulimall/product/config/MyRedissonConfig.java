package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyRedissonConfig {

//	@Value("${ipAddr}")
//	private String ipAddr;

	// redission通过redissonClient对象使用 // 如果是多个redis集群，可以配置
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() {
		Config config = new Config();
		// 创建单例模式的配置
		config.useSingleServer().setAddress("redis://" + "123.56.91.230"+ ":6379");//rediss代表安全模式
		return Redisson.create(config);
	}

}
