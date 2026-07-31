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

    //角色关联表
    @Autowired
    private SysUserRoleMapper userRoleMapper;

    //角色表
    @Autowired
    private SysRoleMapper roleMapper;

    //获取用户分页列表
    @Override
    public IPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, Long roleId, UserStatus status) {
        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUserDomain::getUsername, username);
        }
        if (roleId != null) {
            List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRoleDomain>()
                            .eq(SysUserRoleDomain::getRoleId, roleId))
                    .stream()
                    .map(SysUserRoleDomain::getUserId)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                wrapper.eq(SysUserDomain::getId, -1);
            } else {
                wrapper.in(SysUserDomain::getId, userIds);
            }
        }
        if (status != null) {
            wrapper.eq(SysUserDomain::getStatus, status);
        }
        wrapper.orderByDesc(SysUserDomain::getCreateTime);

        IPage<SysUserDomain> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(this::convertToResponse);
    }

    //添加用户
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
        user.setStatus(request.getStatus() != null ? UserStatus.of(request.getStatus()) : UserStatus.ENABLED);
        this.save(user);

        saveUserRole(user.getId(), request.getRoleId());
    }

    //更新用户
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
        user.setStatus(request.getStatus() != null ? UserStatus.of(request.getStatus()) : UserStatus.ENABLED);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(PasswordUtils.encode(request.getPassword()));
        }
        this.updateById(user);

        if (request.getRoleId() != null) {
            saveUserRole(user.getId(), request.getRoleId());
        }
    }

    //删除用户
    @Override
    @Transactional
    public void deleteUser(Long id) {
        LambdaQueryWrapper<SysUserRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDomain::getUserId, id);
        userRoleMapper.delete(wrapper);
        this.removeById(id);
    }

    //更新用户状态
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

    //重置密码
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

    //根据ID获取用户
    @Override
    public UserVO getUserById(Long id) {
        SysUserDomain user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToResponse(user);
    }

    //根据用户名获取用户
    @Override
    public SysUserDomain getByUsername(String username) {
        LambdaQueryWrapper<SysUserDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserDomain::getUsername, username);
        return this.getOne(wrapper);
    }

    // 获取业主身份的用户列表
    @Override
    public List<UserVO> listOwnerUsers() {
        LambdaQueryWrapper<SysRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRoleDomain::getRoleKey, "owner");
        SysRoleDomain ownerRole = roleMapper.selectOne(roleWrapper);
        if (ownerRole == null) {
            return java.util.Collections.emptyList();
        }
        LambdaQueryWrapper<SysUserRoleDomain> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRoleDomain::getRoleId, ownerRole.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(urWrapper);
        if (userRoles.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Long> userIds = userRoles.stream().map(SysUserRoleDomain::getUserId).collect(Collectors.toList());
        List<SysUserDomain> users = this.listByIds(userIds);
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }


    //保存用户角色
    private void saveUserRole(Long userId, Long roleId) {
        if (roleId == null) {
            return;
        }
        LambdaQueryWrapper<SysUserRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleDomain::getUserId, userId);
        userRoleMapper.delete(wrapper);

        SysUserRoleDomain userRole = new SysUserRoleDomain();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    //转换用户为VO
    private UserVO convertToResponse(SysUserDomain user) {
        UserVO response = new UserVO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());

        LambdaQueryWrapper<SysUserRoleDomain> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysUserRoleDomain::getUserId, user.getId());
        List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(roleWrapper);
        if (!userRoles.isEmpty()) {
            Long roleId = userRoles.get(0).getRoleId();
            response.setRoleId(roleId);
            SysRoleDomain role = roleMapper.selectById(roleId);
            if (role != null) response.setRoleName(role.getRoleName());
        }
        return response;
    }
}
