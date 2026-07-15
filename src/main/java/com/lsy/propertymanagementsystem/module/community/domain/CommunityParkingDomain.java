package com.lsy.propertymanagementsystem.module.community.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.community.enums.ParkingStatus;
import com.lsy.propertymanagementsystem.module.community.enums.ParkingType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community_parking")
public class CommunityParkingDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String parkingNo;
    private ParkingType parkingType;
    private ParkingStatus status;
    private Long ownerId;
    private BigDecimal rentPrice;
    private BigDecimal sellPrice;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}