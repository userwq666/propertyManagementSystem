package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipmentRequest {
    private Long id;

    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @NotBlank(message = "设备编号不能为空")
    private String equipmentCode;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private String location;
    private Integer status;
    private String maintenanceUser;
    private LocalDate installDate;
    private LocalDate warrantyDate;
    private String remark;
}
