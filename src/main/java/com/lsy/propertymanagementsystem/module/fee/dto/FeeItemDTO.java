package com.lsy.propertymanagementsystem.module.fee.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeItemDTO {
    private Long id;

    @NotBlank(message = "收费项目名称不能为空")
    private String itemName;

    private Integer itemType;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    private String unit;

    private Integer cycleType;

    private String description;

    private Integer status;
}