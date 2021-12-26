package com.xjs.log.service;

import java.util.List;
import com.xjs.log.domain.ApiLog;

/**
 * 日志Service接口
 * 
 * @author xjs
 * @date 2021-12-26
 */
public interface IApiLogService 
{
    /**
     * 查询日志
     * 
     * @param id 日志主键
     * @return 日志
     */
    public ApiLog selectApiLogById(Long id);

    /**
     * 查询日志列表
     * 
     * @param apiLog 日志
     * @return 日志集合
     */
    public List<ApiLog> selectApiLogList(ApiLog apiLog);

    /**
     * 批量删除日志
     * 
     * @param ids 需要删除的日志主键集合
     * @return 结果
     */
    public int deleteApiLogByIds(Long[] ids);

    /**
     * 删除日志信息
     * 
     * @param id 日志主键
     * @return 结果
     */
    public int deleteApiLogById(Long id);
}
