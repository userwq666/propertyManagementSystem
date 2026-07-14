package com.lsy.propertymanagementsystem.module.inspection.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inspection_plan")
public class InspectionPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planName;
    private Integer planType;
    private Integer cycleType;
    private Integer cycleValue;
    private LocalDate planDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String equipmentIds;
    private String inspectorIds;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}