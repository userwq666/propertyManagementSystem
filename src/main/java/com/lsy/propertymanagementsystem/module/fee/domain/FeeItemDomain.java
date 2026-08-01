package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeCycleType;
import com.lsy.propertymanagementsystem.module.fee.enums.FeeItemType;
import com.lsy.propertymanagementsystem.module.system.enums.EnableStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_item")
public class FeeItemDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemName;
    private FeeItemType itemType;
    private BigDecimal unitPrice;
    private String unit;
    private FeeCycleType cycleType;
    private String noticeRoles;
    private Integer totalTimes;
    private Integer scopeType;
    private Integer published;
    private String description;
    private EnableStatus status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void changeStatus(EnableStatus status) { this.status = status; }
}
