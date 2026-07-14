package com.lsy.propertymanagementsystem.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.LoginRequest;
import com.lsy.propertymanagementsystem.dto.request.UserRequest;
import com.lsy.propertymanagementsystem.dto.response.LoginResponse;
import com.lsy.propertymanagementsystem.dto.response.UserResponse;
import com.lsy.propertymanagementsystem.module.system.service.AuthService;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SysUserService userService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<UserResponse> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserResponse response = authService.getCurrentUser(token);
        return Result.success(response);
    }
    
    @PostMapping
    public Result<Void> addUser(@RequestBody @Valid UserRequest request) {
        userService.addUser(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateUser(@RequestBody @Valid UserRequest request) {
        userService.updateUser(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    @PutMapping("/status")
    public Result<Void> updateUserStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
    
    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String newPassword = body.get("newPassword").toString();
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    @GetMapping("/page")
    public Result<IPage<UserResponse>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        IPage<UserResponse> page = userService.getUserPage(pageNum, pageSize, username, status);
        return Result.success(page);
    }
}