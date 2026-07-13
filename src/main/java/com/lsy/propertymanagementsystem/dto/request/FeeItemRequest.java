package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeItemRequest {
    private Long id;

    @NotBlank(message = "收费项目名称不能为空")
    private String itemName;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal price;

    @NotNull(message = "收费周期不能为空")
    private Integer cycleType;

    private Integer status;
    private String remark;
}