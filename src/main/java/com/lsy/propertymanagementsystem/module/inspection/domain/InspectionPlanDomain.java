package com.lsy.propertymanagementsystem.module.inspection.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.inspection.enums.FrequencyType;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectionPlanType;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("inspection_plan")
public class InspectionPlanDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planName;
    private InspectionPlanType planType;
    private FrequencyType frequencyType;
    private String frequencyValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private EnableStatus status;
    private String remark;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void changeStatus(EnableStatus status) { this.status = status; }
}