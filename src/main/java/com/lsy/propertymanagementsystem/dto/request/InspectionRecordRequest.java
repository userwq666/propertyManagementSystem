package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InspectionRecordRequest {
    private Long id;
    private Long planId;

    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @NotNull(message = "巡检人员ID不能为空")
    private Long inspectorId;

    @NotNull(message = "巡检结果不能为空")
    private Integer result;

    private String faultDesc;
    private String repairSuggestion;
    private BigDecimal budget;
    private String duration;
}
