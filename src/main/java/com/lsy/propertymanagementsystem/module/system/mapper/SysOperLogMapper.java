package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;
import org.apache.ibatis.annotations.Mapper;

// 系统操作日志Mapper
// 用于操作系统操作日志表
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLogDomain> {
}
