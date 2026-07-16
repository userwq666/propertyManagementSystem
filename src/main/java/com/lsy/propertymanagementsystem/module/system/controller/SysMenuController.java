package com.lsy.propertymanagementsystem.module.system.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.system.dto.MenuRequest;
import com.lsy.propertymanagementsystem.module.system.dto.MenuResponse;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;
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
    public Result<Void> add(@Valid @RequestBody MenuRequest request) {
        menuService.addMenu(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuRequest request) {
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
    public Result<SysMenuDomain> getById(@PathVariable Long id) {
        SysMenuDomain menu = menuService.getMenuById(id);
        return Result.success(menu);
    }

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/tree")
    public Result<List<MenuResponse>> getMenuTree() {
        List<MenuResponse> menus = menuService.getMenuTree();
        return Result.success(menus);
    }

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Result<List<MenuResponse>> getMenuList() {
        List<MenuResponse> menus = menuService.getMenuList();
        return Result.success(menus);
    }
}
