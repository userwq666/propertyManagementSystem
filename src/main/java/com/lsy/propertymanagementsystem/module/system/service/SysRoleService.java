package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.RoleDTO;
import com.lsy.propertymanagementsystem.module.system.dto.RoleVO;

import java.util.List;

public interface SysRoleService {
    //获取角色列表
    List<RoleVO> getRoleList();
    //添加角色
    void addRole(RoleDTO request);
    //更新角色
    void updateRole(RoleDTO request);
    //删除角色
    void deleteRole(Long id);
    //根据ID获取角色
    RoleVO getRoleById(Long id);
    //为角色分配菜单
    void assignMenus(Long roleId, List<Long> menuIds);
    //根据角色ID获取菜单ID列表
    List<Long> getRoleMenuIds(Long roleId);
}