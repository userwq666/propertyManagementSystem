package com.lsy.propertymanagementsystem.module.fee.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FeeRecordDTO {
    private Long id;

    @NotNull(message = "业主id不能为空")
    private Long ownerId;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;

    @NotNull(message = "收费项目id不能为空")
    private Long itemId;

    @NotNull(message = "应收金额不能为空")
    @DecimalMin(value = "0.01", message = "应收金额必须大于0")
    private BigDecimal amount;

    private BigDecimal paidAmount;

    private BigDecimal discountAmount;

    private Integer status;

    private Integer payType;

    private LocalDateTime payTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private String remark;
}