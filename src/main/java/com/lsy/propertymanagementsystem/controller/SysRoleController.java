package com.lsy.propertymanagementsystem.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.dto.request.RoleRequest;
import com.lsy.propertymanagementsystem.entity.SysRole;
import com.lsy.propertymanagementsystem.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    
    @Autowired
    private SysRoleService roleService;
    
    @PostMapping
    public Result<Void> addRole(@RequestBody @Valid RoleRequest request) {
        roleService.addRole(request);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> updateRole(@RequestBody @Valid RoleRequest request) {
        roleService.updateRole(request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }
    
    @GetMapping("/list")
    public Result<List<SysRole>> getRoleList() {
        List<SysRole> roles = roleService.getRoleList();
        return Result.success(roles);
    }
    
    @GetMapping("/{id}")
    public Result<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        return Result.success(role);
    }
    
    @PutMapping("/menu")
    public Result<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }
    
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }
}
