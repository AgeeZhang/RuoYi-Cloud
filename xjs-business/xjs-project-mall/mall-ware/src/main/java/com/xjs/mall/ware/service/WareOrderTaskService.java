package com.xjs.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjs.mall.ware.entity.WareOrderTaskEntity;
import com.xjs.utils.PageUtils;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author xiejs
 * @email 1294405880@qq.com
 * @date 2022-03-15 09:56:19
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}
