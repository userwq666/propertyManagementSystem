package com.lsy.propertymanagementsystem.module.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairRecordDTO {
    private Long id;

    private Long ownerId;

    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    @NotBlank(message = "报修类型不能为空")
    private String repairType;

    @NotBlank(message = "报修描述不能为空")
    private String repairContent;

    private String repairImages;

    private Integer status;

    private Integer priority;

    private Long handlerId;

    private String handleContent;

    private String handleImages;

    private LocalDateTime handleTime;

    private Integer evaluateScore;

    private String evaluateContent;

    private LocalDateTime evaluateTime;
}
