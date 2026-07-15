package com.lsy.propertymanagementsystem.module.inspection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InspectionRecordDTO {
    private Long id;
    private Long planId;

    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @NotNull(message = "巡检人员ID不能为空")
    private Long inspectorUserId;

    @NotNull(message = "巡检结果不能为空")
    private Integer result;

    private String abnormalDesc;
    private String abnormalImages;

    private Integer handleStatus;
    private String handleContent;
    private Long handlerId;

    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private String locationAddress;
    private String remark;
}