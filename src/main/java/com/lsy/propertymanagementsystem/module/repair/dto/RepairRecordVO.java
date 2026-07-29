package com.lsy.propertymanagementsystem.module.repair.dto;

import com.lsy.propertymanagementsystem.module.repair.enums.RepairPriority;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairRecordVO {
    private Long id;
    private String repairNo;
    private Long ownerId;
    private String ownerName;
    private Long houseId;
    private String roomNo;
    private RepairType repairType;
    private String repairContent;
    private String repairImages;
    private RepairStatus status;
    private RepairPriority priority;
    private Long handlerId;
    private String handlerName;
    private String handleContent;
    private String handleImages;
    private LocalDateTime handleTime;
    private Integer evaluateScore;
    private String evaluateContent;
    private LocalDateTime evaluateTime;
    private LocalDateTime createTime;
}
