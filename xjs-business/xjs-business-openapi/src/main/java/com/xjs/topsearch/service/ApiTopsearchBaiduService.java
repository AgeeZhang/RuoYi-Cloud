package com.xjs.topsearch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjs.topsearch.domain.ApiTopsearchBaidu;

/**
 * @author xiejs
 * @since 2022-01-11
 */
public interface ApiTopsearchBaiduService extends IService<ApiTopsearchBaidu> {
    /**
     * 删除百度热搜重复数据
     * @return Integer
     */
    Integer deleteRepeatData();
}
