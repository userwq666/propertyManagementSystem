package com.lsy.propertymanagementsystem.module.equipment.dto;

import com.lsy.propertymanagementsystem.module.equipment.enums.EquipmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EquipmentVO {
    private Long id;
    private String equipmentNo;
    private String equipmentName;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String model;
    private String spec;
    private String location;
    private Long buildingId;
    private String buildingNo;
    private String floor;
    private LocalDate installDate;
    private LocalDate warrantyEndDate;
    private EquipmentStatus status;
    private String qrCode;
    private String remark;
    private LocalDateTime createTime;
}
