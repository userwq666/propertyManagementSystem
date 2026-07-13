package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FeeRecordRequest {
    private Long id;

    @NotNull(message = "业主id不能为空")
    private Long ownerId;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;

    @NotNull(message = "收费项目id不能为空")
    private Long itemId;

    @NotNull(message = "应付总金额不能为空")
    @DecimalMin(value = "0.01", message = "应付总金额必须大于0")
    private BigDecimal totalMoney;

    @NotBlank(message = "账单所属周期不能为空")
    private String billCycle;

    private Integer payStatus;
    private LocalDateTime payTime;
    private String payWay;
}