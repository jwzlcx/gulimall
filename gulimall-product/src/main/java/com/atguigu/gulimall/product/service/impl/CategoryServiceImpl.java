package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService   categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> entities=baseMapper.selectList(null);
        //查到所有一级分类
        List<CategoryEntity> Level1Menus= entities.stream().filter(
                categoryEntity -> categoryEntity.getParentCid()==0
        ).map((menu)->{
            menu.setChildern(getChildrens(menu,entities));
            return menu;
                }
        ).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0: menu1.getSort() )- (menu2.getSort()==null?0: menu2.getSort());
            }
            ).collect(Collectors.toList());


        return Level1Menus;
    }
    //批量的逻辑删除
    @Override
    public void RemoveMenuByIds(List<Long> asList) {
        //TODO 检查被删除的菜单是否被引用
        baseMapper.deleteBatchIds(asList);

    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths =new ArrayList<>();
        List<Long> parentPath= findParentPath(catelogId,paths);//如果当前节点是孙子3，返回的数据是，孙子3，爹9，爷10，但我们要从爷开始
        Collections.reverse(parentPath);//从爷开始
        return parentPath.toArray(new Long[parentPath.size()]);
    }
    /**
     级联更新
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {

        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId=this.getById(catelogId);
        if (byId.getParentCid()!=0)
        {
            findParentPath(byId.getParentCid(),paths);

        }
        return paths;

    }

    //递归查找当前菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all)
    {
        List<CategoryEntity> children =all.stream().filter(categoryEntity ->
        {
            return categoryEntity.getParentCid()==root.getCatId();

        }).map(categoryEntity -> {
            //1找到子菜单
            categoryEntity.setChildern(getChildrens(categoryEntity,all));
            return categoryEntity;
        }
        ).sorted((menu1, menu2)->
        {
            //2菜单排序
            return (menu1.getSort()==null?0: menu1.getSort() )- (menu2.getSort()==null?0: menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}