package com.lsy.propertymanagementsystem.module.equipment.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceStatus;
import com.lsy.propertymanagementsystem.module.equipment.enums.MaintenanceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment_maintenance")
public class EquipmentMaintenanceDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private MaintenanceType maintenanceType;
    private String maintenanceContent;
    private Long maintenancePersonnelId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cost;
    private String partsReplaced;
    private LocalDate nextMaintenanceDate;
    private MaintenanceStatus status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void start() {
        this.status = MaintenanceStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void complete() {
        this.status = MaintenanceStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }
}
