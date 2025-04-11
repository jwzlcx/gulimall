package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryBrandRelationDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    BrandDao  brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationDao   categoryBrandRelationDao;
    @Autowired
    BrandService brandService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId= categoryBrandRelation.getBrandId();
        Long catelogId= categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity=brandDao.selectById(brandId);
        CategoryEntity categoryEntity=categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String name) {
        //根据brandId更改品牌名
        CategoryBrandRelationEntity relationEntity=new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        this.update(relationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId,name);
        CategoryBrandRelationEntity relation=new CategoryBrandRelationEntity();

    }

    @Override
    public List<BrandEntity> categoryBrandRelation(Long catId) {
        //1.通过catid（三级分类名字）在pms_category_brand_relation中找到有这个三级分类的数据，然后封装进List<CategoryBrandRelationEntity>
        List<CategoryBrandRelationEntity> catelogId=categoryBrandRelationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId));
        //2.获得CategoryBrandRelationEntity的brandid，通过brandservice查询对应brandid（品牌id）的entity
        List<BrandEntity> collect=catelogId.stream().map(
                item->{
                    Long brandId=item.getBrandId();
                    BrandEntity brandEntity=brandService.getById(brandId);
                    return brandEntity;
                }
        ).collect(Collectors.toList());
        return collect;
    }

}