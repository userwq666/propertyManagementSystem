package com.lsy.propertymanagementsystem.module.fee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeeExpenseDTO {
    private Long id;

    @NotBlank(message = "支出事项名称不能为空")
    private String expenseName;

    @NotNull(message = "支出类型不能为空")
    private Integer expenseType;

    @NotNull(message = "支出金额不能为空")
    private BigDecimal amount;

    private LocalDate expenseDate;

    private String content;
}
