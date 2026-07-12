package com.lsy.propertymanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lsy.propertymanagementsystem.entity.SysOperLog;

public interface SysOperLogService extends IService<SysOperLog> {
    IPage<SysOperLog> getOperLogPage(Integer pageNum, Integer pageSize);
    
    void addOperLog(String userName, String operModule, String operType, String operIp, String operDesc);
    
    void cleanOperLog();
}
