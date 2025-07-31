package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.config.IocTest;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SpuAttrGroupVo;
import com.atguigu.gulimall.product.vo.skuItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.applet.AppletContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;

import javax.xml.crypto.Data;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);

    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    @Override
    public skuItemVo getItemByid(Long skuId) {
        /*
        * public class ItemSaleAttrsVo {
   private Long attrId;
   private String attrName;
   private List<String> attrValues;
}
*
*
*/

        //基本信息，pms_sku_info;spu销售属性信息;

        skuItemVo skuItemVo = new skuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture =CompletableFuture.supplyAsync(()->{
            SkuInfoEntity skuInfoEntity=getById(skuId);
            skuItemVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);


        //spu商品介绍

        CompletableFuture<Void> c=infoFuture.thenAcceptAsync(res->{
            SpuInfoDescEntity spuInfoDesc =spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuInfoDescEntity(spuInfoDesc);
        },threadPoolExecutor);


        //图片信息pms_sku_images
        CompletableFuture<Void> skuImagesEntityCompletableFuture=infoFuture.runAsync(()->{
            List<SkuImagesEntity> spuImagesEntitys= skuImagesService.getImagesById(skuId);
            skuItemVo.setSkuImagesEntityList(spuImagesEntitys);
        },threadPoolExecutor);


        //获取spu销售属性信息



        //spu规格参数信息
        CompletableFuture<Void> attrFuture=infoFuture.thenAcceptAsync(res->
        {
            List<SpuAttrGroupVo> AttrGroupVos= attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(),res.getCatalogId());
            skuItemVo.setSpuAttrGroupVos(AttrGroupVos);
        },threadPoolExecutor);

        ApplicationContext context=new AnnotationConfigApplicationContext("com.atguigu.gulimall.product.config");
        System.out.println(context.getBean(IocTest.class));

        try {
            CompletableFuture.allOf(c,skuImagesEntityCompletableFuture,attrFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return skuItemVo;
    }


}