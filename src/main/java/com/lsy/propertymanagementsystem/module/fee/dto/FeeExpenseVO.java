package com.lsy.propertymanagementsystem.module.fee.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FeeExpenseVO {
    private Long id;
    private String expenseName;
    private Integer expenseType;
    private String expenseTypeName;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String content;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
}
