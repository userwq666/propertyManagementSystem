package com.lsy.propertymanagementsystem.module.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RepairRecordDTO {
    private Long id;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    @NotBlank(message = "报修类型不能为空")
    private String repairType;

    @NotBlank(message = "报修描述不能为空")
    private String description;

    private String images;

    private Integer status;

    private Long handlerId;

    private LocalDateTime handleTime;

    private BigDecimal cost;

    private String handleResult;

    private Integer rating;

    private String ratingContent;
}