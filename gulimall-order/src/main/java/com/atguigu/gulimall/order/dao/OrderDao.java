package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author jzc
 * @email 2575939509@qq.com
 * @date 2025-03-16 15:31:24
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
