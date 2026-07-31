package com.lsy.propertymanagementsystem.module.equipment.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class EquipmentDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String equipmentNo;
    private String equipmentName;
    private Long categoryId;
    private String brand;
    private String model;
    private String spec;
    private String location;
    private Long buildingId;
    private String floor;
    private LocalDate installDate;
    private EquipmentStatus status;
    private String qrCode;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void changeStatus(EquipmentStatus status) { this.status = status; }
}
