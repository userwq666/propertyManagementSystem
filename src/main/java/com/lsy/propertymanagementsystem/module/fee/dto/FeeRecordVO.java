package com.lsy.propertymanagementsystem.module.fee.dto;

import com.lsy.propertymanagementsystem.module.fee.enums.FeeRecordStatus;
import com.lsy.propertymanagementsystem.module.fee.enums.PayType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FeeRecordVO {
    private Long id;
    private String feeNo;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private Long houseId;
    private String roomNo;
    private Long itemId;
    private String itemName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private FeeRecordStatus status;
    private PayType payType;
    private LocalDateTime payTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
    private LocalDateTime createTime;
}
