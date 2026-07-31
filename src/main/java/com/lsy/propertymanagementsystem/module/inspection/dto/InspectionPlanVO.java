package com.lsy.propertymanagementsystem.module.inspection.dto;

import com.lsy.propertymanagementsystem.module.inspection.enums.FrequencyType;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectionPlanType;
import com.lsy.propertymanagementsystem.module.inspection.enums.PlanStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class InspectionPlanVO {
    private Long id;
    private String planName;
    private InspectionPlanType planType;
    private FrequencyType frequencyType;
    private String frequencyValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private PlanStatus status;
    private String remark;
    private Long creatorId;
    private String creatorName;
    private List<Long> equipmentIds;
    private List<String> equipmentNames;
    private List<Long> inspectorIds;
    private List<String> inspectorNames;
    private LocalDateTime createTime;
}
