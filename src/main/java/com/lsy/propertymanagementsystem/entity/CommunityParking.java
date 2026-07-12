package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_parking")
public class CommunityParking {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String parkingNo;
    
    private Integer parkingType;
    
    private Integer status;
    
    private Long ownerId;
    
    private LocalDateTime expireTime;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
