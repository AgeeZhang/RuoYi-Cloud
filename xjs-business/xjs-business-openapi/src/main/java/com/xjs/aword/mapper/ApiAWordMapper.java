package com.xjs.aword.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjs.aword.domain.ApiAWord;

import java.util.List;

/**
 * mapper
 *
 * @author xiejs
 * @since 2022-01-08
 */
public interface ApiAWordMapper extends BaseMapper<ApiAWord> {


    //---------------------代码生成-------------------------------

    /**
     * 查询每日一句
     *
     * @param id 每日一句主键
     * @return 每日一句
     */
    public ApiAWord selectApiAWordById(Long id);

    /**
     * 查询每日一句列表
     *
     * @param apiAWord 每日一句
     * @return 每日一句集合
     */
    public List<ApiAWord> selectApiAWordList(ApiAWord apiAWord);

    /**
     * 删除每日一句
     *
     * @param id 每日一句主键
     * @return 结果
     */
    public int deleteApiAWordById(Long id);

    /**
     * 批量删除每日一句
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteApiAWordByIds(Long[] ids);
}