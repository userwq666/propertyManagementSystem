package com.lsy.propertymanagementsystem.module.system.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.common.utils.SecurityUtils;
import com.lsy.propertymanagementsystem.module.system.dto.MenuDTO;
import com.lsy.propertymanagementsystem.module.system.dto.MenuVO;
import com.lsy.propertymanagementsystem.module.system.service.SysMenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody MenuDTO request) {
        menuService.addMenu(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuDTO request) {
        menuService.updateMenu(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:menu:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/{id}")
    public Result<MenuVO> getById(@PathVariable Long id) {
        MenuVO menu = menuService.getMenuById(id);
        return Result.success(menu);
    }

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/tree")
    public Result<List<MenuVO>> getMenuTree() {
        List<MenuVO> menus = menuService.getMenuTree();
        return Result.success(menus);
    }

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Result<List<MenuVO>> getMenuList() {
        List<MenuVO> menus = menuService.getMenuList();
        return Result.success(menus);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-tree")
    public Result<List<MenuVO>> getUserMenuTree() {
        return Result.success(menuService.getUserMenuTree(SecurityUtils.getCurrentUserId()));
    }
}
