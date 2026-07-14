package com.lsy.propertymanagementsystem.module.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("community_house")
public class CommunityHouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long buildingId;
    
    private String roomNo;
    
    private BigDecimal area;
    
    private String houseType;
    
    private Integer houseStatus;
    
    private Long ownerId;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
