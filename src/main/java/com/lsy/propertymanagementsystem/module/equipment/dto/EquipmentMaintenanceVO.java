package com.lsy.propertymanagementsystem.module.equipment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EquipmentMaintenanceVO {
    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private Integer maintenanceType;
    private String maintenanceContent;
    private Long maintenancePersonnelId;
    private String maintenancePersonnelName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cost;
    private String partsReplaced;
    private LocalDate nextMaintenanceDate;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
