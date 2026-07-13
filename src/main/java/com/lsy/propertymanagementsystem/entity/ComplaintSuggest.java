package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}