package com.xjs.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjs.mall.to.SkuReductionTo;
import com.xjs.utils.PageUtils;
import com.xjs.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author xiejs
 * @email 1294405880@qq.com
 * @date 2022-03-15 10:25:21
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存sku优惠满减信息
     * @param skuReductionTo 优惠满减To
     */
    void saveSkuReduction(SkuReductionTo skuReductionTo);
}
