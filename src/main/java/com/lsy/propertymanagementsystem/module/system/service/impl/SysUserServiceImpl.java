package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.module.system.entity.SysUser;
import com.lsy.propertymanagementsystem.module.system.entity.SysUserRole;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    
    @Override
    public IPage<UserResponse> getUserPage(Integer pageNum, Integer pageSize, String username, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        
        return page.convert(this::convertToResponse);
    }
    
    @Override
    @Transactional
    public void addUser(UserRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        this.save(user);
        
        saveUserRoles(user.getId(), request.getRoleIds());
    }
    
    @Override
    @Transactional
    public void updateUser(UserRequest request) {
        SysUser user = this.getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        wrapper.ne(SysUser::getId, request.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());
        user.setStatus(request.getStatus());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(PasswordUtils.encode(request.getPassword()));
        }
        this.updateById(user);
        
        if (request.getRoleIds() != null) {
            saveUserRoles(user.getId(), request.getRoleIds());
        }
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
        this.removeById(id);
    }
    
    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        this.updateById(user);
    }
    
    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(PasswordUtils.encode(newPassword));
        this.updateById(user);
    }
    
    @Override
    public UserResponse getUserById(Long id) {
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToResponse(user);
    }
    
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }
    
    private UserResponse convertToResponse(SysUser user) {
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