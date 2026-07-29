package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.common.utils.PasswordUtils;
import com.lsy.propertymanagementsystem.module.system.dto.UserDTO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.enums.UserType;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDomain> implements SysUserService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public IPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, UserStatus status) {
        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUserDomain::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUserDomain::getStatus, status);
        }
        wrapper.orderByDesc(SysUserDomain::getCreateTime);

        IPage<SysUserDomain> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(this::convertToResponse);
    }

    @Override
    @Transactional
    public void addUser(UserDTO request) {
        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDomain::getUsername, request.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUserDomain user = new SysUserDomain();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(UserType.of(request.getUserType()));
        user.setStatus(request.getStatus() != null ? UserStatus.of(request.getStatus()) : UserStatus.ENABLED);
        this.save(user);

        saveUserRoles(user.getId(), request.getRoleIds());
    }

    @Override
    @Transactional
    public void updateUser(UserDTO request) {
        SysUserDomain user = this.getById(request.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDomain::getUsername, request.getUsername());
        wrapper.ne(SysUserDomain::getId, request.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setUserType(UserType.of(request.getUserType()));
        user.setStatus(request.getStatus() != null ? UserStatus.of(request.getStatus()) : UserStatus.ENABLED);
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
        LambdaQueryWrapper<SysUserRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDomain::getUserId, id);
        userRoleMapper.delete(wrapper);
        this.removeById(id);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, UserStatus status) {
        SysUserDomain user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.changeStatus(status);
        this.updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        SysUserDomain user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.resetPassword(PasswordUtils.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUserDomain user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToResponse(user);
    }

    @Override
    public SysUserDomain getByUsername(String username) {
        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDomain::getUsername, username);
        return this.getOne(wrapper);
    }

    @Override
    public SysUserDomain getById(Long id) {
        return super.getById(id);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysUserRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDomain::getUserId, userId);
        userRoleMapper.delete(wrapper);

        for (Long roleId : roleIds) {
            SysUserRoleDomain userRole = new SysUserRoleDomain();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    private UserVO convertToResponse(SysUserDomain user) {
        UserVO response = new UserVO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());

        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRoleDomain::getRoleId).collect(Collectors.toList());
        response.setRoleIds(roleIds);

        if (!roleIds.isEmpty()) {
            LambdaQueryWrapper<SysRoleDomain> roleQueryWrapper = new LambdaQueryWrapper<>();
            roleQueryWrapper.in(SysRoleDomain::getId, roleIds);
            response.setRoles(roleMapper.selectList(roleQueryWrapper).stream()
                    .map(SysRoleDomain::getRoleName)
                    .collect(Collectors.toList()));
        }

        return response;
    }
}