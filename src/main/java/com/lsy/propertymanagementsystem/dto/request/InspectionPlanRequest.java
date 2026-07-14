package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InspectionPlanRequest {
    private Long id;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @NotNull(message = "计划类型不能为空")
    private Integer planType;

    private Integer cycleType;
    private Integer cycleValue;
    private LocalDate planDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String equipmentIds;
    private String inspectorIds;
}
