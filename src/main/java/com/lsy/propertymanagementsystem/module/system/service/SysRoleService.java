package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.RoleDTO;
import com.lsy.propertymanagementsystem.module.system.dto.RoleVO;

import java.util.List;

public interface SysRoleService {
    List<RoleVO> getRoleList();
    void addRole(RoleDTO request);
    void updateRole(RoleDTO request);
    void deleteRole(Long id);
    RoleVO getRoleById(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
    List<Long> getRoleMenuIds(Long roleId);
}