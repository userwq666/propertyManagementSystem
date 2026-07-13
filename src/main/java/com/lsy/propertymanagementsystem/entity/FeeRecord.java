package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private Long itemId;
    private BigDecimal totalMoney;
    private String billCycle;
    private Integer payStatus;
    private LocalDateTime payTime;
    private String payWay;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
