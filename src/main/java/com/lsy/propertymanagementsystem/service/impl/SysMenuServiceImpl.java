package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.entity.SysMenu;
import com.lsy.propertymanagementsystem.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    
    @Override
    public List<MenuResponse> getMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }
    
    @Override
    public List<MenuResponse> getMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }
    
    @Override
    @Transactional
    public void addMenu(MenuRequest request) {
        SysMenu menu = new SysMenu();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(request.getMenuType());
        menu.setSort(request.getSort());
        menu.setStatus(request.getStatus());
        this.save(menu);
    }
    
    @Override
    @Transactional
    public void updateMenu(MenuRequest request) {
        SysMenu menu = this.getById(request.getId());
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(request.getMenuType());
        menu.setSort(request.getSort());
        menu.setStatus(request.getStatus());
        this.updateById(menu);
    }
    
    @Override
    @Transactional
    public void deleteMenu(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        this.removeById(id);
    }
    
    @Override
    public SysMenu getMenuById(Long id) {
        return this.getById(id);
    }
    
    private List<MenuResponse> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuResponse> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuResponse response = new MenuResponse();
                response.setId(menu.getId());
                response.setParentId(menu.getParentId());
                response.setMenuName(menu.getMenuName());
                response.setPath(menu.getPath());
                response.setComponent(menu.getComponent());
                response.setPerms(menu.getPerms());
                response.setMenuType(menu.getMenuType());
                response.setSort(menu.getSort());
                response.setStatus(menu.getStatus());
                response.setChildren(buildMenuTree(menus, menu.getId()));
                tree.add(response);
            }
        }
        return tree;
    }
}
