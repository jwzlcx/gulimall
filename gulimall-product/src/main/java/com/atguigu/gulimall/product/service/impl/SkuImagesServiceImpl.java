package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.SpuImagesDao;
import com.atguigu.gulimall.product.entity.SpuImagesEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuImagesDao;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuImagesEntity> getImagesById(Long skuId) {
        SkuImagesDao skuImagesDao=this.baseMapper;
        List<SkuImagesEntity> ImagesEntities=skuImagesDao.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));

        return ImagesEntities;
    }

}