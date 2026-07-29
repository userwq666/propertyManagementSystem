package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleKey;
    private String remark;
    private LocalDateTime createTime;
}
