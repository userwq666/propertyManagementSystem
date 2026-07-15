package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RoleRequest {
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "权限标识不能为空")
    private String roleKey;

    private String remark;
}