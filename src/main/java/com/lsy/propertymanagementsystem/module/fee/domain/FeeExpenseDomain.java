package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_expense")
public class FeeExpenseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String expenseName;
    private Integer expenseType;
    private BigDecimal amount;
    private Integer auditStatus;
    private Long auditorId;
    private LocalDateTime auditTime;
    private LocalDate expenseDate;
    private String content;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
