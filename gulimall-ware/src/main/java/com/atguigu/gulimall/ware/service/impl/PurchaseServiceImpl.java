package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService detailService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
      IPage<PurchaseEntity> page=this.page(
              new Query<PurchaseEntity>().getPage(params),
              new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
              //采购单刚新建或者刚分配给某人，但是某人未领取任务
      );
      return new PageUtils(page);
    }

    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId=mergeVo.getPurchaseId();
        if(purchaseId==null)
        {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());// 新建状态

            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items=mergeVo.getItems();
        Long fPurchaseId=purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity=new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(fPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        detailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity=new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

    @Override
    public void recevied(List<Long> ids) {
        //1、确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect=ids.stream().map(
                id->{
                    PurchaseEntity entitybyId=this.getById(id);
                    return entitybyId;
                }
        ).filter(
                item->{
                    if(item.getStatus()==WareConstant.PurchaseStatusEnum.CREATED.getCode()||
                    item.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){return true;}
                    return false;

                }
        ).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //改变采购单状态
        this.updateBatchById(collect);

        //改变采购项状态
        collect.forEach((item)->
        {
            List<PurchaseDetailEntity> entities = detailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities=entities.stream().map(
                    entity->{
                        PurchaseDetailEntity entity1=new PurchaseDetailEntity();
                        entity1.setId(entity.getId());
                        entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                        return entity1;
                    }
            ).collect(Collectors.toList());
            detailService.updateBatchById(detailEntities);
        });

    }

}