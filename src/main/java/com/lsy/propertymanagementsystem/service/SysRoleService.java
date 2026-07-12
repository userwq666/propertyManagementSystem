package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> getRoleList();
    
    void addRole(RoleRequest request);
    
    void updateRole(RoleRequest request);
    
    void deleteRole(Long id);
    
    SysRole getRoleById(Long id);
    
    void assignMenus(Long roleId, List<Long> menuIds);
    
    List<Long> getRoleMenuIds(Long roleId);
}
