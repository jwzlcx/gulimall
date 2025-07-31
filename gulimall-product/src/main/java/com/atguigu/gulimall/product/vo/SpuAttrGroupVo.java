package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;
@Data
public class SpuAttrGroupVo {
    private String attrName;
    private List<SpuBaseAttrVo> spuBaseAttrVoList;

}

