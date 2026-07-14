package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.module.system.entity.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {
    IPage<SysOperLog> getOperLogPage(Integer pageNum, Integer pageSize);
    
    void addOperLog(SysOperLog operLog);
    
    void cleanOperLog();
}