package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class SysOperLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String userName;
    
    private String operModule;
    
    private String operType;
    
    private String operIp;
    
    private String operDesc;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}