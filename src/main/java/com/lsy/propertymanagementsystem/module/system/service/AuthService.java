package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    
    void logout(String token);
    
    UserResponse getCurrentUser(String token);
}
