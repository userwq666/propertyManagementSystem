package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RoleDTO {
    // 角色ID
    private Long id;
    // 角色名称
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    // 权限标识
    @NotBlank(message = "权限标识不能为空")
    private String roleKey;
    // 备注
    private String remark;
}