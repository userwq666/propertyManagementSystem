package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.MenuRequest;
import com.lsy.propertymanagementsystem.dto.response.MenuResponse;
import com.lsy.propertymanagementsystem.entity.SysMenu;
import com.lsy.propertymanagementsystem.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class SysMenuController {
    
    @Autowired
    private SysMenuService menuService;
    
    @PostMapping
    public Result<Void> addMenu(@RequestBody @Valid MenuRequest request) {
        menuService.addMenu(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateMenu(@RequestBody @Valid MenuRequest request) {
        menuService.updateMenu(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<MenuResponse>> getMenuList() {
        List<MenuResponse> menus = menuService.getMenuList();
        return Result.success(menus);
    }
    
    @GetMapping("/{id}")
    public Result<SysMenu> getMenuById(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuById(id);
        return Result.success(menu);
    }
    
    @GetMapping("/tree")
    public Result<List<MenuResponse>> getMenuTree() {
        List<MenuResponse> menus = menuService.getMenuTree();
        return Result.success(menus);
    }
}
