package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class MenuRequest {
    private Long id;
    
    private Long parentId;
    
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;
    
    private String path;
    
    private String component;
    
    private String perms;
    
    private Integer menuType;
    
    private Integer sort;
    
    private Integer status;
}
