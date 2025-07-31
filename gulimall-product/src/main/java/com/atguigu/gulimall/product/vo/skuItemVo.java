package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.*;
import lombok.Data;

import java.util.List;
@Data
public class skuItemVo {
    //基本信息，pms_sku_infospu
    SkuInfoEntity skuInfoEntity;

    //图片信息pms_sku_images
    List<SkuImagesEntity> skuImagesEntityList;

    //获取spu销售属性组合
    List<ItemSaleAttrsVo> itemSaleAttrsVo;
    //spu的介绍
    SpuInfoDescEntity spuInfoDescEntity;

    //spu的规格参数信息
    List<SpuAttrGroupVo> spuAttrGroupVos;



}
