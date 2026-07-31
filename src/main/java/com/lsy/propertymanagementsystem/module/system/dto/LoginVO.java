package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {
    // 登录凭证
    private String token;
    // 用户ID
    private Long userId;
    // 用户名
    private String username;
    // 真实姓名
    private String realName;
    // 头像
    private String avatar;
    // 权限列表
    private List<String> permissions;
    // 角色列表
    private List<String> roles;
}