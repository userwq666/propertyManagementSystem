package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    
    private Long menuId;
}