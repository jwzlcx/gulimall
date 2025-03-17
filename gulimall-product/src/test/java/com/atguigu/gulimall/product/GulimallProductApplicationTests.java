package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.service.BrandService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Test
    public void contextLoads() {

    }

}
