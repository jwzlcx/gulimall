package com.atguigu.gulimall.product.service.impl;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import com.atguigu.common.utils.PageUtils;
@Component
@Aspect
@ComponentScan

public class AopLogger {
    @Before("execution(public void com.atguigu.gulimall.product.service.impl.AttrAttrgroupRelationServiceImpl.saveBatch())")
    public void saveBatchBefore(JoinPoint joinpoint)
    {
        String name= joinpoint.getSignature().getName();
        System.out.println(name+"开始");
    }
    @After("execution(public void com.atguigu.gulimall.product.service.impl.AttrAttrgroupRelationServiceImpl.saveBatch())")
    public void saveBatchAfter(JoinPoint joinpoint)
    {
        String name= joinpoint.getSignature().getName();
        System.out.println(name+"结束");
    }
}
