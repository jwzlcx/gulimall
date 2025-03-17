package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jzc
 * @email 2575939509@qq.com
 * @date 2025-03-16 16:45:59
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
