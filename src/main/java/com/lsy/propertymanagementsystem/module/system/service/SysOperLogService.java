package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;

public interface SysOperLogService {
    //获取操作日志分页列表
    IPage<SysOperLogDomain> getOperLogPage(Integer pageNum, Integer pageSize, String userName, String operModule);
    //删除操作日志
    void deleteOperLog(Long id);
    //清理过期操作日志
    void cleanOperLog(Integer days);
}