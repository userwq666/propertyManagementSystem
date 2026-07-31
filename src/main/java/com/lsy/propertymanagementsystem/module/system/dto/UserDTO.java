package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserDTO {
    // 用户ID
    private Long id;
    // 用户名
    @NotBlank(message = "用户名不能为空")
    private String username;
    // 密码
    private String password;
    // 真实姓名
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    // 手机号
    @NotBlank(message = "手机号不能为空")
       private String phone;
    // 角色ID
    private Long roleId;
    // 状态
    private Integer status;
}