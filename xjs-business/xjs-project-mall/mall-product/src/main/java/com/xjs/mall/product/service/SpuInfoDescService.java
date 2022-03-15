package com.xjs.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjs.utils.PageUtils;
import com.xjs.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author xiejs
 * @email 1294405880@qq.com
 * @date 2022-03-15 10:16:53
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

