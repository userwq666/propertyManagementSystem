package com.lsy.propertymanagementsystem.module.community.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.community.enums.HouseStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community_house")
public class CommunityHouseDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long buildingId;
    private String roomNo;
    private BigDecimal area;
    private String houseType;
    private HouseStatus houseStatus;
    private Long ownerId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}