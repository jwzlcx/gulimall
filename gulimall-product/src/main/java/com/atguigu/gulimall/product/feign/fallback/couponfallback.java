package com.atguigu.gulimall.product.feign.fallback;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class couponfallback implements CouponFeignService {
    @Override
    public R saveSpuBounds(SpuBoundTo spuBoundTo) {
        log.error("熔断");
        return null;
    }

    @Override
    public R saveSkuReduction(SkuReductionTo skuReductionTo) {
        return null;
    }


}
