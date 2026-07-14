package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.module.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<MenuResponse> getMenuTree();
    
    List<MenuResponse> getMenuList();
    
    void addMenu(MenuRequest request);
    
    void updateMenu(MenuRequest request);
    
    void deleteMenu(Long id);
    
    SysMenu getMenuById(Long id);
}
