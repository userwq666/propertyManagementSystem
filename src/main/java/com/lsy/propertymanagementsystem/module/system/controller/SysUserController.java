package com.lsy.propertymanagementsystem.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.system.dto.UserDTO;
import com.lsy.propertymanagementsystem.module.system.dto.UserVO;
import com.lsy.propertymanagementsystem.module.system.enums.UserStatus;
import com.lsy.propertymanagementsystem.module.system.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody UserDTO request) {
        userService.addUser(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO request) {
        userService.updateUser(request);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:user:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        UserVO response = userService.getUserById(id);
        return Result.success(response);
    }

    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/page")
    public Result<IPage<UserVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        IPage<UserVO> page = userService.getUserPage(pageNum, pageSize, username, status != null ? UserStatus.of(status) : null);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, UserStatus.of(status));
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/password")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String newPassword = (String) body.get("newPassword");
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
}
