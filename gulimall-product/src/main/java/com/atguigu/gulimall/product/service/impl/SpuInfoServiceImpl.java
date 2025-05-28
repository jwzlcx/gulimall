package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.annotation.TableId;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    BrandService  brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        //1.保存spu基本信息pms_spu_info
        SpuInfoEntity infoEntity=new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2.保存spu的描述图片pms_spu_info_desc
        List<String> decript=vo.getDecript();
        SpuInfoDescEntity descEntity=new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3.保存spu图片集pms_spu_images
        List<String> images=vo.getImages();
        imagesService.saveImages(infoEntity.getId(),images);
        //4.保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs=vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect=baseAttrs.stream().map(
                attr->{
                    ProductAttrValueEntity attrValueEntity=new ProductAttrValueEntity();
                    attrValueEntity.setAttrId(attr.getAttrId());
                    AttrEntity attrEntityById=attrService.getById(attr.getAttrId());
                    attrValueEntity.setAttrName(attrEntityById.getAttrName());
                    attrValueEntity.setAttrValue(attr.getAttrValues());
                    attrValueEntity.setQuickShow(attr.getShowDesc());
                    attrValueEntity.setSpuId(infoEntity.getId());
                    return attrValueEntity;
                }
        ).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //5、保存当前spu的sku信息
        List<Skus> skus=vo.getSkus();
        if(skus!=null&&skus.size()>0)
        {
            skus.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }

                //    private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                SkuInfoEntity skuInfoEntity=new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                //5.1）、sku的基本信息；pms_sku_info
                Long skuId=skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities=item.getImages().stream().map(
                        img->{
                            SkuImagesEntity skuImagesEntity=new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuId);
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());
                            return skuImagesEntity;
                        }
                ).collect(Collectors.toList());
                //5.2）、sku的图片信息；pms_sku_image
                skuImagesService.saveBatch(imagesEntities);


                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities=attr.stream().map(
                        sale->{
                            SkuSaleAttrValueEntity skuSaleAttrValueEntity=new SkuSaleAttrValueEntity();
                            BeanUtils.copyProperties(sale,skuSaleAttrValueEntity);
                            skuSaleAttrValueEntity.setSkuId(skuId);
                            return skuSaleAttrValueEntity;
                        }
                ).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //TODO 要先判断有没有必要保存再保存

                Bounds bounds=vo.getBounds();
                SpuBoundTo spuBoundTo=new SpuBoundTo();
                BeanUtils.copyProperties(bounds,spuBoundTo);
                spuBoundTo.setSpuId(infoEntity.getId());
                couponFeignService.saveSpuBounds(spuBoundTo);

                SkuReductionTo skuReductionTo=new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });





        }


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);


    }

    @Override
    public void up(Long spuId) {
        //查出spuId对应的所有sku(红色的256g苹果14)信息
        List<SkuInfoEntity> skus=skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuids = skus.stream().map(
                sku -> sku.getSkuId()
        ).collect(Collectors.toList());
        List<ProductAttrValueEntity> baseAttrs=productAttrValueService.baseAttrlistforspu(spuId);

        List<Long> attrIds = baseAttrs.stream().map(
                attr->
                {
                   return attr.getAttrId();
                }
        ).collect(Collectors.toList());
        // 过滤出可被检索的基本属性id，即search_type = 1 [数据库中目前 4、5、6、11不可检索]

        List<Long> searchAttrIds=attrService.selectSearchAttrs(attrIds);

        Set<Long> idSet = new HashSet<>(searchAttrIds);
        // 可被检索的属性封装到SkuEsModel.Attrs中
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs1);
                    return attrs1;
                }).collect(Collectors.toList());
//         每件skuId是否有库存

        Map<Long, Boolean> stockMap = null;
        try {
            // 3.1 远程调用库存系统 查询该sku是否有库存
            R<List<SkuHasStockVo>> hasStock = wareFeignService.getSkuHasStock(skuids);
            // 构造器受保护 所以写成内部类对象
            stockMap = hasStock.getData(new TypeReference<List<SkuHasStockVo>>() {})
                    .stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            log.warn("服务调用成功" + hasStock);
        } catch (Exception e) {
            log.error("库存服务调用失败: 原因{}", e);
        }

        Map<Long, Boolean> finalStockMap = stockMap;
        // 开始封装es
        List<SkuEsModel> skuEsModels = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            // 4 设置库存，只查是否有库存，不查有多少
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // TODO 1.热度评分  刚上架是0
            esModel.setHotScore(0L);
            // 设置品牌信息
            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            // 查询分类信息
            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            // 保存商品的属性，  查询当前sku的所有可以被用来检索的规格属性，同一spu都一样，在外面查一遍即可
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());

//         5.发给ES进行保存  gulimall-search
        R r = searchFeignService.productStatusUp(skuEsModels);
        if (r.getCode() == 0) {
            // 远程调用成功
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败
            /**
             * Feign 的调用流程  Feign有自动重试机制
             * 1. 发送请求执行
             * 2.
             */
        }





    }


}