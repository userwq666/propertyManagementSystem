package com.lsy.propertymanagementsystem.module.equipment.domain;

import com.baomidou.mybatisplus.annotation.*;
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void start() {
        this.status = 1;
        this.startTime = LocalDateTime.now();
    }

    public void complete() {
        this.status = 2;
        this.endTime = LocalDateTime.now();
    }
}
