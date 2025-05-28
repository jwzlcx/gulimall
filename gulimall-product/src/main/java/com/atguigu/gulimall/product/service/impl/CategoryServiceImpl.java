package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog3Vo;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Autowired
    StringRedisTemplate redisTemplate;
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
    @CacheEvict(value = "category",key = "'getLevelOneCategory'")
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {

        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }

    /**
     * 		[分区名默认是就是缓存的前缀] SpringCache: 不加锁
     *
     * @CacheEvict: 缓存失效模式		--- 页面一修改 然后就清除这两个缓存
     * key = "'getLevel1Categorys'" : 记得加单引号 [子解析字符串]
     *
     * @Caching: 同时进行多种缓存操作
     *
     * @CacheEvict(value = {"category"}, allEntries = true) : 删除这个缓存分区所有数据,缓存失效模式
     *
     * @CachePut: 这次查询操作写入缓存,双写模式
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevelOneCategory() {

        List<CategoryEntity> categoryEntities=baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));

        return categoryEntities;
    }
    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> entityList, Long parent_cid) {

        return entityList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
    }
    @Override
    public Map<String,List<Catelog2Vo>> getCatalogJson(){
        //1.从缓存中拿json，有的话反序列化成对象
        //2.没有，从数据库中查，查到后序列化成json存入redis，
        //json跨语言
        String catalogJson=redisTemplate.opsForValue().get("CatalogJson");
        if (catalogJson.isEmpty())
        {
            Map<String,List<Catelog2Vo>> catalogJsonfronDb=getCatalogJsondb();

            return catalogJsonfronDb;
        }
        Map<String,List<Catelog2Vo>> res=JSON.parseObject(catalogJson,new TypeReference<Map<String,List<Catelog2Vo>>>(){});
        return res;
    }

    public Map<String,List<Catelog2Vo>> getCatalogJsondb() {
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String s= JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJson",s);
        return parent_cid;
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
    //TODO:1、配置gulimall.com对应的ip地址（nginx的ip地址） 2、在ngnix配置代理，把这个gulimall.com请求转发给product服务
    //TODO nginx动静分离、反向代理，内网穿透(完成)



}