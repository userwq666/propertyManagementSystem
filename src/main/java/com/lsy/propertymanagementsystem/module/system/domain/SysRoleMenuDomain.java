package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_role_menu")    // 角色菜单关联表sys_role_menu
public class SysRoleMenuDomain {
    @TableId(type = IdType.AUTO)                // 主键自增
    // 角色菜单关联ID
    private Long id;
    // 角色ID
    private Long roleId;
    // 菜单ID
    private Long menuId;
}
