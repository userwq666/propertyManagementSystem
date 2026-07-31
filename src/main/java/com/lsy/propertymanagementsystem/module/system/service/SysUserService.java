package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.module.system.dto.UserDTO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import java.util.List;
import java.util.List;

public interface SysUserService {
    // 获取用户分页列表
    IPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, UserStatus status);
    // 添加用户
    void addUser(UserDTO request);
    // 更新用户
    void updateUser(UserDTO request);
    // 删除用户
    void deleteUser(Long id);
    // 更新用户状态
    void updateUserStatus(Long id, UserStatus status);
    // 重置密码
    void resetPassword(Long id, String newPassword);
    // 根据ID获取用户
    UserVO getUserById(Long id);
    // 根据用户名获取用户
    SysUserDomain getByUsername(String username);
    List<UserVO> listOwnerUsers();
}