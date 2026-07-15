package com.lsy.propertymanagementsystem.module.system.dto;

import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.system.enums.MenuType;
import lombok.Data;

import java.util.List;

@Data
public class MenuResponse {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String perms;
    private MenuType menuType;
    private Integer sort;
    private EnableStatus status;
    private List<MenuResponse> children;
}