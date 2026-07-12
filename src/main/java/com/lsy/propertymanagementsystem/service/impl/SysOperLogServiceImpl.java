package com.lsy.propertymanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.entity.SysOperLog;
import com.lsy.propertymanagementsystem.mapper.SysOperLogMapper;
import com.lsy.propertymanagementsystem.service.SysOperLogService;
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
    public void addOperLog(String userName, String operModule, String operType, String operIp, String operDesc) {
        SysOperLog log = new SysOperLog();
        log.setUserName(userName);
        log.setOperModule(operModule);
        log.setOperType(operType);
        log.setOperIp(operIp);
        log.setOperDesc(operDesc);
        this.save(log);
    }
    
    @Override
    @Transactional
    public void cleanOperLog() {
        this.remove(null);
    }
}
