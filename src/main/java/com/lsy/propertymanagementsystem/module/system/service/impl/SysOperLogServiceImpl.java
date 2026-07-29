package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysOperLogMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    @Override
    public IPage<SysOperLogDomain> getOperLogPage(Integer pageNum, Integer pageSize, String userName, String operModule) {
        LambdaQueryWrapper<SysOperLogDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(userName != null && !userName.isEmpty(), SysOperLogDomain::getUserName, userName);
        wrapper.like(operModule != null && !operModule.isEmpty(), SysOperLogDomain::getOperModule, operModule);
        wrapper.orderByDesc(SysOperLogDomain::getCreateTime);
        Page<SysOperLogDomain> page = new Page<>(pageNum, pageSize);
        return operLogMapper.selectPage(page, wrapper);
    }

    @Override
    public void deleteOperLog(Long id) {
        operLogMapper.deleteById(id);
    }

    @Override
    public void cleanOperLog(Integer days) {
        LambdaQueryWrapper<SysOperLogDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperLogDomain::getCreateTime, LocalDateTime.now().minusDays(days));
        operLogMapper.delete(wrapper);
    }
}