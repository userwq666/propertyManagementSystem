package com.lsy.propertymanagementsystem.module.complaint.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("complaint_suggest")
public class ComplaintSuggest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private String type;
    private String title;
    private String content;
    private Integer status;
    private String handleUser;
    private String handleResult;
    private LocalDateTime finishTime;
    private Integer rating;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}