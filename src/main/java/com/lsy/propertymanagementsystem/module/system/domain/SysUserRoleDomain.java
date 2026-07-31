package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_user_role")                  // 用户角色关联表sys_user_role
public class SysUserRoleDomain {
    @TableId(type = IdType.AUTO)                // 主键自增
    // 用户角色关联ID
    private Long id;
    // 用户ID
    private Long userId;
    // 角色ID
    private Long roleId;
}
