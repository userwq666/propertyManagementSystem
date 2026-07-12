package com.lsy.propertymanagementsystem.dto.response;

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
    private Integer menuType;
    private Integer sort;
    private Integer status;
    private List<MenuResponse> children;
}
