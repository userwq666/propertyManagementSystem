package com.lsy.propertymanagementsystem.module.inspection.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("inspection_plan_equipment")
public class InspectionPlanEquipmentDomain {
    @TableId(type = IdType.NONE)
    private Long planId;
    private Long equipmentId;
}