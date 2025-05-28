package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.demos.web.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;
    @Test
    public void indexData() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");   //数据的id

        User user = new User();
        user.setName("zhangsan");
        user.setAge(18);
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString, XContentType.JSON);

        IndexResponse index = client.index(request, GulimallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);

    }
    //TODO:127章test没有做

    @Test
    public void contextLoads() {

        System.out.println(client);
    }

}
