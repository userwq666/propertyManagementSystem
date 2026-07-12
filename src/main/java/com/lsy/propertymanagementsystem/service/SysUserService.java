package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    IPage<UserResponse> getUserPage(Integer pageNum, Integer pageSize, String username, Integer status);
    
    void addUser(UserRequest request);
    
    void updateUser(UserRequest request);
    
    void deleteUser(Long id);
    
    void updateUserStatus(Long id, Integer status);
    
    void resetPassword(Long id, String newPassword);
    
    UserResponse getUserById(Long id);
}
