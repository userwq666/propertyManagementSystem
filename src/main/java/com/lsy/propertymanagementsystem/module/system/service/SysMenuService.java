package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.MenuDTO;
import com.lsy.propertymanagementsystem.module.system.dto.MenuVO;

import java.util.List;

public interface SysMenuService {
    List<MenuVO> getMenuTree();
    List<MenuVO> getMenuList();
    void addMenu(MenuDTO request);
    void updateMenu(MenuDTO request);
    void deleteMenu(Long id);
    MenuVO getMenuById(Long id);
}