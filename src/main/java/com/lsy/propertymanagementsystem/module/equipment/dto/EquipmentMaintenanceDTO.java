package com.lsy.propertymanagementsystem.module.equipment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EquipmentMaintenanceDTO {
    private Long id;

    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @NotNull(message = "维护类型不能为空")
    private Integer maintenanceType;

    private String maintenanceContent;
    private Long maintenancePersonnelId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cost;
    private String partsReplaced;
    private LocalDate nextMaintenanceDate;
    private Integer status;
    private String remark;
}
