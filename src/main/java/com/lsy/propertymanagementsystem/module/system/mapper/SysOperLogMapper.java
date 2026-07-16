package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysOperLogDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLogDomain> {
}
