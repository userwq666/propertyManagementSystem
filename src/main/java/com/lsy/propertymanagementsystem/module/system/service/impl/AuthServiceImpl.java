package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.result.ResultCode;
import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.module.system.dto.LoginDTO;
import com.lsy.propertymanagementsystem.module.system.dto.LoginVO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleMenuDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.enums.UserType;
import com.lsy.propertymanagementsystem.module.system.mapper.SysMenuMapper;
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
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysMenuMapper menuMapper;

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

        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRoleDomain::getRoleId).collect(Collectors.toList());

        List<String> permissions = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            LambdaQueryWrapper<SysRoleMenuDomain> rmWrapper = new LambdaQueryWrapper<>();
            rmWrapper.in(SysRoleMenuDomain::getRoleId, roleIds);
            List<SysRoleMenuDomain> roleMenus = roleMenuMapper.selectList(rmWrapper);
            List<Long> menuIds = roleMenus.stream().map(SysRoleMenuDomain::getMenuId).distinct().collect(Collectors.toList());

            if (!menuIds.isEmpty()) {
                LambdaQueryWrapper<SysMenuDomain> menuWrapper = new LambdaQueryWrapper<>();
                menuWrapper.in(SysMenuDomain::getId, menuIds);
                menuWrapper.isNotNull(SysMenuDomain::getPerms);
                menuWrapper.ne(SysMenuDomain::getPerms, "");
                menuMapper.selectList(menuWrapper).forEach(menu -> permissions.add(menu.getPerms()));
            }
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType().getValue(), permissions);

        LoginVO response = new LoginVO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setUserType(user.getUserType());
        response.setAvatar(user.getAvatar());
        response.setRoleIds(roleIds);
        response.setPermissions(permissions);
        response.setRoles(getRoleNames(user.getUserType()));

        return response;
    }

    private List<String> getRoleNames(UserType userType) {
        List<String> roleNames = new ArrayList<>();
        if (userType == UserType.SUPER_ADMIN) {
            roleNames.add("admin");
        } else if (userType == UserType.PROPERTY_ADMIN) {
            roleNames.add("property");
        } else if (userType == UserType.OWNER) {
            roleNames.add("owner");
        } else if (userType == UserType.REPAIR_WORKER) {
            roleNames.add("repair_worker");
        } else if (userType == UserType.INSPECTOR) {
            roleNames.add("inspector");
        }
        return roleNames;
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            JwtUtils.invalidateToken(token);
        }
    }

    @Override
    public UserVO getCurrentUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long userId = JwtUtils.getUserId(token);
        SysUserDomain user = userService.getById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO response = new UserVO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setUserType(UserType.of(user.getUserType().getValue()));
        response.setStatus(UserStatus.of(user.getStatus().getValue()));
        response.setCreateTime(user.getCreateTime());

        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        response.setRoleIds(userRoles.stream().map(SysUserRoleDomain::getRoleId).collect(Collectors.toList()));

        response.setRoles(getRoleNames(user.getUserType()));

        return response;
    }
}
