package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.enums.PayType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecordDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String feeNo;
    private Long ownerId;
    private Long houseId;
    private Long itemId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private FeeRecordStatus status;
    private PayType payType;
    private LocalDateTime payTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void confirmPay(PayType payType) { this.status = FeeRecordStatus.PAID; this.paidAmount = this.amount; this.payType = payType; this.payTime = LocalDateTime.now(); }
    public void markPartialPay(BigDecimal paidAmount, PayType payType) { this.status = FeeRecordStatus.PARTIAL_PAID; this.paidAmount = paidAmount; this.payType = payType; this.payTime = LocalDateTime.now(); }
    public void markOverdue() { if (this.status == FeeRecordStatus.UNPAID) this.status = FeeRecordStatus.OVERDUE; }
    public void cancel() { this.status = FeeRecordStatus.CANCELLED; }
}