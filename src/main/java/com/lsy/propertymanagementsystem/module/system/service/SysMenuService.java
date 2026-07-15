package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.MenuRequest;
import com.lsy.propertymanagementsystem.module.system.dto.MenuResponse;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;

import java.util.List;

public interface SysMenuService {
    List<MenuResponse> getMenuTree();
    List<MenuResponse> getMenuList();
    void addMenu(MenuRequest request);
    void updateMenu(MenuRequest request);
    void deleteMenu(Long id);
    SysMenuDomain getMenuById(Long id);
}