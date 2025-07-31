package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;

@Component
@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //System.out.println("queryPage 开始");
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        //System.out.println("queryPage 结束");
        return new PageUtils(page);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
//        System.out.println("saveBatch 开始");
        List<AttrAttrgroupRelationEntity> collect=vos.stream().map(
                (item)->{
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity=new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(vos,attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                }
                ).collect(Collectors.toList());
        this.saveBatch(collect);
//       System.out.println("saveBatch 结束");
    }

}