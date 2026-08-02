package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.result.ResultCode;
import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.module.system.dto.LoginDTO;
import com.lsy.propertymanagementsystem.module.system.dto.LoginVO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.AuthService;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    // 认证服务实现类
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserServiceImpl sysUserServiceImpl;
    // 用户服务
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    // 用户角色映射器
    @Autowired
    private SysRoleMapper roleMapper;
    // 角色映射器
    @Autowired
    private SysMenuMapper menuMapper;

    // 登录
    @Override
    public LoginVO login(LoginDTO request) {
        SysUserDomain user = userService.getByUsername(request.getUsername());

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        if (!PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 获取用户角色
        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRoleDomain::getRoleId).collect(Collectors.toList());

        // 获取角色标识用于 JWT
        String roleKey = null;
        if (!roleIds.isEmpty()) {
            SysRoleDomain role = roleMapper.selectById(roleIds.get(0));
            if (role != null) roleKey = role.getRoleKey();
        }
        // 获取用户权限
        List<String> permissions = menuMapper.selectPermsByUserId(user.getId());

        // 包含用户ID、用户名、角色标识、权限列表
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), roleKey, permissions);

        // 构建登录响应
        LoginVO response = new LoginVO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setAvatar(user.getAvatar());
        response.setPermissions(permissions);
        response.setRoles(getRoleKeys(roleIds));

        // 返回登录响应
        return response;
    }

    // 退出登录
    @Override
    public void logout(String token, String tabId) {
        if (token != null) {
            JwtUtils.invalidateToken(token, tabId);
        }
    }

    // 获取当前用户信息
    @Override
    public UserVO getCurrentUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        // 从 JWT 中获取用户ID
        Long userId = JwtUtils.getUserId(token);
        SysUserDomain user = sysUserServiceImpl.getById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 构建用户响应
        UserVO response = new UserVO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());

        // 获取用户角色
        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRoleDomain::getRoleId).collect(Collectors.toList());
        if (!roleIds.isEmpty()) {
            response.setRoleId(roleIds.get(0));
            SysRoleDomain role = roleMapper.selectById(roleIds.get(0));
            if (role != null) response.setRoleName(role.getRoleName());
        }
        response.setRoles(getRoleKeys(roleIds));
        // 获取用户权限
        List<String> permissions = menuMapper.selectPermsByUserId(userId);
        response.setPermissions(permissions);

        // 返回用户响应
        return response;
    }


    // 获取角色名称列表
    private List<String> getRoleKeys(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleDomain::getId, roleIds);
        return roleMapper.selectList(wrapper).stream()
                .map(SysRoleDomain::getRoleKey)
                .collect(Collectors.toList());
    }

}
