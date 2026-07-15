package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.RoleRequest;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;

import java.util.List;

public interface SysRoleService {
    List<SysRoleDomain> getRoleList();
    void addRole(RoleRequest request);
    void updateRole(RoleRequest request);
    void deleteRole(Long id);
    SysRoleDomain getRoleById(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
    List<Long> getRoleMenuIds(Long roleId);
}