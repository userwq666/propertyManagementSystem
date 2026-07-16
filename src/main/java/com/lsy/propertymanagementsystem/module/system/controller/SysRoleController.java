package com.lsy.propertymanagementsystem.module.system.controller;

import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.system.dto.RoleRequest;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.service.SysRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody RoleRequest request) {
        roleService.addRole(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleRequest request) {
        roleService.updateRole(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:role:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/{id}")
    public Result<SysRoleDomain> getById(@PathVariable Long id) {
        SysRoleDomain role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Result<List<SysRoleDomain>> getRoleList() {
        List<SysRoleDomain> roles = roleService.getRoleList();
        return Result.success(roles);
    }

    @PreAuthorize("hasAuthority('system:role:edit')")
    @PostMapping("/assignMenus")
    public Result<Void> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }
}
