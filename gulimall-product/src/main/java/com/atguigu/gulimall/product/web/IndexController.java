package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisTemplate  redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model)
    {
        List<CategoryEntity> categoryEntities =categoryService.getLevelOneCategory();
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatalogJson(){
        Map<String,List<Catelog2Vo>> map=categoryService.getCatalogJson();
        return map;
    }
    @ResponseBody
    @GetMapping("/index/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 设置这里有5个人
        door.trySetCount(5);
        door.await();

        return "5个人全部通过了...";
    }

    @ResponseBody
    @GetMapping("/index/go/{id}")
    public String go(@PathVariable("id") Long id) {

        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 每访问一次相当于出去一个人
        door.countDown();
        return id + "走了";
    }
}
