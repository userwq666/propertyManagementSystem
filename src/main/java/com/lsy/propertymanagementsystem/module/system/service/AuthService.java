package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.LoginDTO;
import com.lsy.propertymanagementsystem.module.system.dto.LoginVO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;

public interface AuthService {
    LoginVO login(LoginDTO request);
    void logout(String token);
    UserVO getCurrentUser(String token);
}