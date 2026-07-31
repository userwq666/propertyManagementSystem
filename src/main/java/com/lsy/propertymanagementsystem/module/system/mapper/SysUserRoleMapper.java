package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import org.apache.ibatis.annotations.Mapper;

// 用户角色Mapper
// 用于操作用户角色关联表
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleDomain> {
}
