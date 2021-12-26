package com.xjs.log.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xjs.log.mapper.ApiLogMapper;
import com.xjs.log.domain.ApiLog;
import com.xjs.log.service.IApiLogService;

/**
 * 日志Service业务层处理
 *
 * @author xjs
 * @date 2021-12-26
 */
@Service
public class ApiLogServiceImpl implements IApiLogService {
    @Autowired
    private ApiLogMapper apiLogMapper;

    /**
     * 查询日志
     *
     * @param id 日志主键
     * @return 日志
     */
    @Override
    public ApiLog selectApiLogById(Long id) {
        return apiLogMapper.selectApiLogById(id);
    }

    /**
     * 查询日志列表
     *
     * @param apiLog 日志
     * @return 日志
     */
    @Override
    public List<ApiLog> selectApiLogList(ApiLog apiLog) {
        return apiLogMapper.selectApiLogList(apiLog);
    }

    /**
     * 批量删除日志
     *
     * @param ids 需要删除的日志主键
     * @return 结果
     */
    @Override
    public int deleteApiLogByIds(Long[] ids) {
        return apiLogMapper.deleteApiLogByIds(ids);
    }

    /**
     * 删除日志信息
     *
     * @param id 日志主键
     * @return 结果
     */
    @Override
    public int deleteApiLogById(Long id) {
        return apiLogMapper.deleteApiLogById(id);
    }
}
