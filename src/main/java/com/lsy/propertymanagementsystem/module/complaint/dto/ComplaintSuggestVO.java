package com.lsy.propertymanagementsystem.module.complaint.dto;

import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintPriority;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplaintSuggestVO {
    private Long id;
    private String complaintNo;
    private Long ownerId;
    private String ownerName;
    private Long houseId;
    private String roomNo;
    private ComplaintType type;
    private String category;
    private String content;
    private String images;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private Long handlerId;
    private String handlerName;
    private String handleContent;
    private LocalDateTime handleTime;
    private Integer isAnonymous;
    private LocalDateTime createTime;
}
