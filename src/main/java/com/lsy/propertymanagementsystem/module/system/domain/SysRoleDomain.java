package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")                  // 角色表sys_role
public class SysRoleDomain {
    @TableId(type = IdType.AUTO)                // 主键自增
    // 角色ID
    private Long id;
    // 角色名称
    private String roleName;
    // 角色权限标识
    private String roleKey;
    // 夒色备注
    private String remark;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)             // 插入时填充当前时间
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)        // 更新时填充当前时间
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
