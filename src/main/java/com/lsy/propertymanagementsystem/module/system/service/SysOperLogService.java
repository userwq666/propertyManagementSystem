package com.lsy.propertymanagementsystem.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;

public interface SysOperLogService {
    IPage<SysOperLogDomain> getOperLogPage(Integer pageNum, Integer pageSize, String userName, String operModule);
    void deleteOperLog(Long id);
    void cleanOperLog(Integer days);
}