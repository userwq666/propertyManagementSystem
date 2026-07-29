package com.lsy.propertymanagementsystem.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;
import com.lsy.propertymanagementsystem.module.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/operLog")
public class SysOperLogController {

    @Autowired
    private SysOperLogService operLogService;

    @PreAuthorize("hasAuthority('system:operLog:list')")
    @GetMapping("/page")
    public Result<IPage<SysOperLogDomain>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String operModule) {
        IPage<SysOperLogDomain> page = operLogService.getOperLogPage(pageNum, pageSize, userName, operModule);
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('system:operLog:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operLogService.deleteOperLog(id);
        return Result.success();
    }

    @PreAuthorize("hasAuthority('system:operLog:delete')")
    @DeleteMapping("/clean")
    public Result<Void> clean(@RequestParam(defaultValue = "90") Integer days) {
        operLogService.cleanOperLog(days);
        return Result.success();
    }
}