package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inspection_record")
public class InspectionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long equipmentId;
    private Long inspectorId;
    private LocalDateTime inspectTime;
    private Integer result;
    private String faultDesc;
    private String repairSuggestion;
    private BigDecimal budget;
    private String duration;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}