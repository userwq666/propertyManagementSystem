package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuDomain> {

    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
            "JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.perms IS NOT NULL AND m.perms != '' AND m.deleted = 0")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}