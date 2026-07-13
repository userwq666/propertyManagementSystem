package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_item")
public class FeeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemName;
    private BigDecimal price;
    private Integer cycleType;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
