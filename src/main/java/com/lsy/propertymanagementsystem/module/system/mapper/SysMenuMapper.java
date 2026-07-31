package com.lsy.propertymanagementsystem.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsy.propertymanagementsystem.module.system.domain.SysMenuDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 菜单Mapper
// 用于操作菜单表
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenuDomain> {
    // 根据用户ID查询用户权限
    @Select("SELECT DISTINCT m.perms FROM sys_menu m " +
            "JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.perms IS NOT NULL AND m.perms != '' AND m.deleted = 0")
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    // 根据用户ID查询其有权限的目录和菜单
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "WHERE m.menu_type IN (0, 1) AND m.status = 1 AND m.deleted = 0 " +
            "AND (m.id = 22 " +
            "     OR EXISTS (SELECT 1 FROM sys_user_role ur " +
            "               WHERE ur.user_id = #{userId} AND ur.role_id IN (1, 2)) " +
            "     OR EXISTS (SELECT 1 FROM sys_user_role ur2 " +
            "               JOIN sys_role_menu rm2 ON rm2.role_id = ur2.role_id " +
            "               WHERE ur2.user_id = #{userId} AND rm2.menu_id = m.id)) " +
            "ORDER BY m.sort")
    List<SysMenuDomain> selectUserMenus(@Param("userId") Long userId);
}
