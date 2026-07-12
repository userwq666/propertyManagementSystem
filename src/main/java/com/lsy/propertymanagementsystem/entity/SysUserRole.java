package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class SysUserRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long roleId;
}