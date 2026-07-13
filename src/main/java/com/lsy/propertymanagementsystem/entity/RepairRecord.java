package com.lsy.propertymanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair_record")
public class RepairRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerId;
    private Long houseId;
    private String repairType;
    private String content;
    private String imgUrl;
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