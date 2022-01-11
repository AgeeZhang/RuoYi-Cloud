package com.xjs.topsearch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjs.topsearch.domain.ApiTopsearchAllnetwork;

/**
 * 全网热搜service
 * @author xiejs
 * @since 2022-01-10
 */
public interface ApiTopsearchAllnetworkService extends IService<ApiTopsearchAllnetwork> {

    /**
     * 删除全网热搜重复数据
     * @return Integer
     */
    Integer deleteRepeatData();
}
