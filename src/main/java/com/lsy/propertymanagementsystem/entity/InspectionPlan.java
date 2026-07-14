package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}