package com.lsy.propertymanagementsystem.module.inspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class InspectionPlanDTO {
    private Long id;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @NotNull(message = "计划类型不能为空")
    private Integer planType;

    private Integer frequencyType;
    private String frequencyValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private Integer status;

    private String remark;
    private Long creatorId;

    private List<Long> equipmentIds;
    private List<Long> inspectorIds;
}