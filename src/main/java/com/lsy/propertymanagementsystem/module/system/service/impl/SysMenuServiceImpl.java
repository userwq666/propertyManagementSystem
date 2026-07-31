package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.system.dto.MenuDTO;
import com.lsy.propertymanagementsystem.module.system.dto.MenuVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.system.enums.MenuType;
import com.lsy.propertymanagementsystem.module.system.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuDomain> implements SysMenuService {

    // 构建菜单树
    @Override
    public List<MenuVO> getMenuTree() {
        LambdaQueryWrapper<SysMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuDomain::getStatus, EnableStatus.ENABLED);
        wrapper.orderByAsc(SysMenuDomain::getSort);
        List<SysMenuDomain> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }

    // 获取菜单列表
    @Override
    public List<MenuVO> getMenuList() {
        LambdaQueryWrapper<SysMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenuDomain::getSort);
        List<SysMenuDomain> menus = this.list(wrapper);
        return buildMenuTree(menus, 0L);
    }

    // 获取当前用户有权限的菜单树
    @Override
    public List<MenuVO> getUserMenuTree(Long userId) {
        List<SysMenuDomain> menus = baseMapper.selectUserMenus(userId);
        return buildMenuTree(menus, 0L);
    }

    // 添加菜单
    @Override
    @Transactional
    public void addMenu(MenuDTO request) {
        SysMenuDomain menu = new SysMenuDomain();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setIcon(request.getIcon());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(MenuType.of(request.getMenuType()));
        menu.setSort(request.getSort());
        menu.setStatus(EnableStatus.of(request.getStatus()));
        this.save(menu);
    }

    // 更新菜单
    @Override
    @Transactional
    public void updateMenu(MenuDTO request) {
        SysMenuDomain menu = this.getById(request.getId());
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setIcon(request.getIcon());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPerms(request.getPerms());
        menu.setMenuType(MenuType.of(request.getMenuType()));
        menu.setSort(request.getSort());
        menu.setStatus(EnableStatus.of(request.getStatus()));
        this.updateById(menu);
    }

    // 删除菜单
    @Override
    @Transactional
    public void deleteMenu(Long id) {
        LambdaQueryWrapper<SysMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuDomain::getParentId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        this.removeById(id);
    }

    // 根据ID获取菜单
    @Override
    public MenuVO getMenuById(Long id) {
        SysMenuDomain menu = this.getById(id);
        if (menu == null) {
            return null;
        }
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setIcon(menu.getIcon());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPerms(menu.getPerms());
        vo.setMenuType(menu.getMenuType());
        vo.setSort(menu.getSort());
        vo.setStatus(menu.getStatus());
        return vo;
    }

    // 构建菜单树
    private List<MenuVO> buildMenuTree(List<SysMenuDomain> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (SysMenuDomain menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuVO response = new MenuVO();
                response.setId(menu.getId());
                response.setParentId(menu.getParentId());
                response.setMenuName(menu.getMenuName());
                response.setIcon(menu.getIcon());
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
