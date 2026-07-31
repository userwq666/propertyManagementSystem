package com.lsy.propertymanagementsystem.module.fee.dto;

import com.lsy.propertymanagementsystem.module.fee.enums.FeeCycleType;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeItemType;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FeeItemVO {
    private Long id;
    private String itemName;
    private FeeItemType itemType;
    private BigDecimal unitPrice;
    private String unit;
    private FeeCycleType cycleType;
    private Integer dueDay;
    private String noticeRoles;
    private Integer totalTimes;
    private String description;
    private EnableStatus status;
    private LocalDateTime createTime;
}
