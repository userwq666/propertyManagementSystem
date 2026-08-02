package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.LoginDTO;
import com.lsy.propertymanagementsystem.module.system.dto.LoginVO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;

// 认证服务接口
public interface AuthService {
    // 登录
    LoginVO login(LoginDTO request);
    // 退出登录
    void logout(String token, String tabId);
    // 获取当前用户信息
    UserVO getCurrentUser(String token);
}
