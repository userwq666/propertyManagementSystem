package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String phone;

    private Integer userType;

    private Integer status;

    private List<Long> roleIds;
}