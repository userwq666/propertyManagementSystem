package com.lsy.propertymanagementsystem.module.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")                  // 操作日志表sys_oper_log
public class SysOperLogDomain {
    @TableId(type = IdType.AUTO)                // 主键自增
    // 操作日志ID
    private Long id;
    // 用户ID
    private Long userId;
    // 用户名
    private String userName;
    // 操作模块
    private String operModule;
    // 操作类型
    private String operType;
    // 操作描述
    private String operDesc;
    // 创建时间
    @TableField(fill = FieldFill.INSERT)    // 插入时填充当前时间
    private LocalDateTime createTime;
}
