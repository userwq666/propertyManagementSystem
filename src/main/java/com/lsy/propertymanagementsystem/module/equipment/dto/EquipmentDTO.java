package com.lsy.propertymanagementsystem.module.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipmentDTO {
    private Long id;

    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private String brand;
    private String model;
    private String spec;
    private String location;
    private Long buildingId;
    private String floor;
    private LocalDate installDate;
    private LocalDate warrantyEndDate;

    private Integer status;

    private String qrCode;
    private String remark;
}