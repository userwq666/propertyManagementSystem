package com.lsy.propertymanagementsystem.module.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_building")
public class CommunityBuilding {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String buildingNo;
    
    private Integer floorCount;
    
    private Integer totalHouse;
    
    private Integer buildYear;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
