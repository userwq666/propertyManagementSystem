package com.lsy.propertymanagementsystem.module.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class MenuDTO {
    // 菜单ID
    private Long id;

    // 父菜单ID
    private Long parentId;

    // 菜单名称
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    // 菜单图标
    private String icon;

    // 路径
    private String path;

    // 组件
    private String component;

    // 权限
    private String perms;

    // 菜单类型
    private Integer menuType;

    // 排序
    private Integer sort;

    // 状态
    private Integer status;
}
