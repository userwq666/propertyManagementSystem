package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import org.apache.ibatis.annotations.Mapper;

// 角色Mapper
// 用于操作角色表
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleDomain> {
}
