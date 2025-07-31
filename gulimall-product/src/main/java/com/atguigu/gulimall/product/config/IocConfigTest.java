package com.atguigu.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IocConfigTest {
    @Bean(value = "iocTest")
    public  IocTest datacon(){
        IocTest iocTest=new IocTest();
        iocTest.setId(5);
        iocTest.setName("daa");

        return iocTest;
    }
}
