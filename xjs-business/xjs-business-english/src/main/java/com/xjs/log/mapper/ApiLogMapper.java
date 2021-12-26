package com.xjs.log.mapper;

import java.util.List;
import com.xjs.log.domain.ApiLog;

/**
 * 日志Mapper接口
 * 
 * @author xjs
 * @date 2021-12-26
 */
public interface ApiLogMapper 
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
     * 删除日志
     * 
     * @param id 日志主键
     * @return 结果
     */
    public int deleteApiLogById(Long id);

    /**
     * 批量删除日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteApiLogByIds(Long[] ids);
}
