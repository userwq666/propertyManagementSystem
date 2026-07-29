package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.module.system.dto.UserDTO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserDomain;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;

public interface SysUserService {
    IPage<UserVO> getUserPage(Integer pageNum, Integer pageSize, String username, UserStatus status);
    void addUser(UserDTO request);
    void updateUser(UserDTO request);
    void deleteUser(Long id);
    void updateUserStatus(Long id, UserStatus status);
    void resetPassword(Long id, String newPassword);
    UserVO getUserById(Long id);
    SysUserDomain getByUsername(String username);
    SysUserDomain getById(Long id);
}