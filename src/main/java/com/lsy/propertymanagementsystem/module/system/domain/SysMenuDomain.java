package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import com.lsy.propertymanagementsystem.module.system.enums.MenuType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenuDomain {
    // 菜单ID
    @TableId(type = IdType.AUTO)
    private Long id;
    // 父菜单ID
    private Long parentId;
    // 菜单名称
    private String menuName;
    // 路径
    private String path;
    // 组件路径
    private String component;
    // 权限字符串
    private String perms;
    // 菜单类型
    private MenuType menuType;
    // 排序
    private Integer sort;
    // 启用状态
    private EnableStatus status;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 删除状态
    private Integer deleted;
}