package com.lsy.propertymanagementsystem.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.common.result.Result;
import com.lsy.propertymanagementsystem.module.system.entity.SysOperLog;
import com.lsy.propertymanagementsystem.module.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operlog")
public class SysOperLogController {
    
    @Autowired
    private SysOperLogService operLogService;
    
    @GetMapping("/page")
    public Result<IPage<SysOperLog>> getOperLogPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SysOperLog> page = operLogService.getOperLogPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @DeleteMapping("/clean")
    public Result<Void> cleanOperLog() {
        operLogService.cleanOperLog();
        return Result.success();
    }
}
