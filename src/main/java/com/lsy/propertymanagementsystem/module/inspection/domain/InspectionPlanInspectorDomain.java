package com.lsy.propertymanagementsystem.module.inspection.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("inspection_plan_inspector")
public class InspectionPlanInspectorDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long inspectorId;
}