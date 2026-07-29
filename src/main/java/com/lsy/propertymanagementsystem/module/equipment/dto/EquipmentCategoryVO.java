package com.lsy.propertymanagementsystem.module.equipment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentCategoryVO {
    private Long id;
    private String categoryName;
    private Long parentId;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
