package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.module.system.dto.UserRequest;
import com.lsy.propertymanagementsystem.module.system.dto.UserResponse;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.enums.UserType;

public interface SysUserService {
    IPage<UserResponse> getUserPage(Integer pageNum, Integer pageSize, String username, UserStatus status);
    void addUser(UserRequest request);
    void updateUser(UserRequest request);
    void deleteUser(Long id);
    void updateUserStatus(Long id, UserStatus status);
    void resetPassword(Long id, String newPassword);
    UserResponse getUserById(Long id);
    SysUserDomain getByUsername(String username);
    SysUserDomain getById(Long id);
}