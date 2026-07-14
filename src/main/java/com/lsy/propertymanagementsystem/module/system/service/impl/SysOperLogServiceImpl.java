package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.module.system.entity.SysOperLog;
import com.lsy.propertymanagementsystem.module.system.mapper.SysOperLogMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysOperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
    
    @Override
    public IPage<SysOperLog> getOperLogPage(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysOperLog::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    @Transactional
    public void addOperLog(SysOperLog operLog) {
        this.save(operLog);
    }
    
    @Override
    @Transactional
    public void cleanOperLog() {
        this.remove(null);
    }
}