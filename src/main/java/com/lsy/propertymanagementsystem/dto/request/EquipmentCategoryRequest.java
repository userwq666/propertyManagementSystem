package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentCategoryRequest {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String description;
    private Integer sortOrder;
}
