package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.module.system.entity.SysRole;
import com.lsy.propertymanagementsystem.module.system.entity.SysRoleMenu;
import com.lsy.propertymanagementsystem.module.system.entity.SysUserRole;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Override
    public List<SysRole> getRoleList() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getId);
        return this.list(wrapper);
    }
    
    @Override
    @Transactional
    public void addRole(RoleRequest request) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, request.getRoleName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.save(role);
    }
    
    @Override
    @Transactional
    public void updateRole(RoleRequest request) {
        SysRole role = this.getById(request.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, request.getRoleName());
        wrapper.ne(SysRole::getId, request.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.updateById(role);
    }
    
    @Override
    @Transactional
    public void deleteRole(Long id) {
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getRoleId, id);
        if (userRoleMapper.selectCount(userRoleWrapper) > 0) {
            throw new BusinessException("该角色下存在用户，不允许删除");
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, id);
        roleMenuMapper.delete(wrapper);
        
        this.removeById(id);
    }
    
    @Override
    public SysRole getRoleById(Long id) {
        return this.getById(id);
    }
    
    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
        
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }
    
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }
}