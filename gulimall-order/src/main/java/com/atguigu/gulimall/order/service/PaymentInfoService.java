package com.atguigu.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author jzc
 * @email 2575939509@qq.com
 * @date 2025-03-16 15:31:23
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

