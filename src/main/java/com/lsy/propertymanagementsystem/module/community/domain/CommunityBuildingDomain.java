package com.lsy.propertymanagementsystem.module.community.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_building")
public class CommunityBuildingDomain {
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