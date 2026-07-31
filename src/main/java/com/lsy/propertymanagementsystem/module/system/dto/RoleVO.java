package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleVO {
    // 角色ID
    private Long id;
    // 角色名称
    private String roleName;
    // 权限标识
    private String roleKey;
    // 备注
    private String remark;
    // 创建时间
    private LocalDateTime createTime;
}
