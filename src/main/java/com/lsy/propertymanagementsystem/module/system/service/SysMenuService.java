package com.lsy.propertymanagementsystem.module.system.service;

import com.lsy.propertymanagementsystem.module.system.dto.MenuDTO;
import com.lsy.propertymanagementsystem.module.system.dto.MenuVO;

import java.util.List;

// 菜单服务接口
public interface SysMenuService {
    // 获取菜单树
    List<MenuVO> getMenuTree();
    // 获取菜单列表
    List<MenuVO> getMenuList();
    // 添加菜单
    void addMenu(MenuDTO request);
    // 更新菜单
    void updateMenu(MenuDTO request);
    // 删除菜单
    void deleteMenu(Long id);
    // 根据ID获取菜单
    MenuVO getMenuById(Long id);
}