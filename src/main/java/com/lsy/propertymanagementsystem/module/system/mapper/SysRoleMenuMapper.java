package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleMenuDomain;
import org.apache.ibatis.annotations.Mapper;

// 角色菜单关联Mapper
// 用于操作角色菜单关联表
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenuDomain> {
}
