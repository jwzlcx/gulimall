package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuid
         * wareid
         */
        QueryWrapper<WareSkuEntity> queryWrapper=new QueryWrapper<>();
        String skuId=(String) params.get("skuId");

        if (!StringUtils.isEmpty(skuId))
        {
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId=(String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId))
        {
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {

        //检查，查询当前sku的库存量：库存量减去被锁定的量
        List<SkuHasStockVo> collect=skuIds.stream().map(
                skuId->{
                    SkuHasStockVo skuHasStockVo=new SkuHasStockVo();
                    Long count=baseMapper.getSkuStock(skuId);
                    skuHasStockVo.setSkuId(skuId);
                    skuHasStockVo.setHasStock(count==null?false:count>0);
                    return skuHasStockVo;
        }
        ).collect(Collectors.toList());
        return collect;
    }

}