package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.LoginRequest;
import com.lsy.propertymanagementsystem.module.system.dto.LoginResponse;
import com.lsy.propertymanagementsystem.module.system.dto.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    UserResponse getCurrentUser(String token);
}