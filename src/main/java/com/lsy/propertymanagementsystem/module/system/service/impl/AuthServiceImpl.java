package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.module.system.entity.SysMenu;
import com.lsy.propertymanagementsystem.module.system.entity.SysRole;
import com.lsy.propertymanagementsystem.module.system.entity.SysRoleMenu;
import com.lsy.propertymanagementsystem.module.system.entity.SysUser;
import com.lsy.propertymanagementsystem.module.system.entity.SysUserRole;
import com.lsy.propertymanagementsystem.module.system.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.AuthService;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;
    
    @Autowired
    private SysMenuMapper menuMapper;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userService.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setUserType(user.getUserType());
        response.setAvatar(user.getAvatar());
        
        LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        response.setRoleIds(roleIds);
        
        List<String> permissions = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            LambdaQueryWrapper<SysRoleMenu> rmWrapper = new LambdaQueryWrapper<>();
            rmWrapper.in(SysRoleMenu::getRoleId, roleIds);
            List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(rmWrapper);
            List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
            
            if (!menuIds.isEmpty()) {
                LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
                menuWrapper.in(SysMenu::getId, menuIds);
                menuWrapper.isNotNull(SysMenu::getPerms);
                menuWrapper.ne(SysMenu::getPerms, "");
                menuMapper.selectList(menuWrapper).forEach(menu -> permissions.add(menu.getPerms()));
            }
        }
        response.setPermissions(permissions);
        
        return response;
    }
    
    @Override
    public void logout(String token) {
        JwtUtils.invalidateToken(token);
    }
    
    @Override
    public UserResponse getCurrentUser(String token) {
        Long userId = JwtUtils.getUserId(token);
        SysUser user = userService.getById(userId);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        
        LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRole::getUserId, user.getId());
        List<SysUserRole> userRoles = userRoleMapper.selectList(roleWrapper);
        response.setRoleIds(userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
        
        return response;
    }
}