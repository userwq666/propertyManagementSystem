package com.lsy.propertymanagementsystem.module.system.dto;

import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.system.enums.MenuType;
import lombok.Data;

import java.util.List;

@Data
public class MenuVO {
    // 菜单ID
    private Long id;
    // 父菜单ID
    private Long parentId;
    // 菜单名称
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
    private MenuType menuType;
    // 排序
    private Integer sort;
    // 状态
    private EnableStatus status;
    // 子菜单
    private List<MenuVO> children;
}
