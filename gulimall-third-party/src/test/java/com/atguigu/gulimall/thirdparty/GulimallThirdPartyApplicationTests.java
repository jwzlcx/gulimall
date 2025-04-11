package com.atguigu.gulimall.thirdparty;


import com.aliyun.oss.OSS;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class GulimallThirdPartyApplicationTests {
    @Autowired
    OSS ossClient;

    @Test
    void contextLoads() {
    }
    @Test
    public void testUpload() throws FileNotFoundException {
        InputStream inputStream=new FileInputStream("E:\\谷粒商城\\谷粒商城资料整理课件\\基础篇\\资料\\pics\\oppo.png");
        ossClient.putObject("overwatchguli","oppo.png",inputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }


}
