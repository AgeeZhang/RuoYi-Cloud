package com.bwie.user.mapper;

import java.util.List;
import com.bwie.user.domain.TbRolePerm;

/**
 * 角色权限Mapper接口
 * 
 * @author xs
 * @date 2023-01-15
 */
public interface TbRolePermMapper 
{
    /**
     * 查询角色权限
     * 
     * @param rolePermId 角色权限主键
     * @return 角色权限
     */
    public TbRolePerm selectTbRolePermByRolePermId(Long rolePermId);

    /**
     * 查询角色权限列表
     * 
     * @param tbRolePerm 角色权限
     * @return 角色权限集合
     */
    public List<TbRolePerm> selectTbRolePermList(TbRolePerm tbRolePerm);

    /**
     * 新增角色权限
     * 
     * @param tbRolePerm 角色权限
     * @return 结果
     */
    public int insertTbRolePerm(TbRolePerm tbRolePerm);

    /**
     * 修改角色权限
     * 
     * @param tbRolePerm 角色权限
     * @return 结果
     */
    public int updateTbRolePerm(TbRolePerm tbRolePerm);

    /**
     * 删除角色权限
     * 
     * @param rolePermId 角色权限主键
     * @return 结果
     */
    public int deleteTbRolePermByRolePermId(Long rolePermId);

    /**
     * 批量删除角色权限
     * 
     * @param rolePermIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTbRolePermByRolePermIds(Long[] rolePermIds);
}