package com.lsy.propertymanagementsystem.module.inspection.dto;

import com.lsy.propertymanagementsystem.module.inspection.enums.HandleStatus;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.enums.TaskStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InspectionRecordVO {
    private Long id;
    private Long planId;
    private String planName;
    private Long equipmentId;
    private String equipmentName;
    private Long inspectorUserId;
    private String inspectorName;
    private Long fillerUserId;
    private String fillerName;
    private LocalDateTime inspectionTime;
    private TaskStatus taskStatus;
    private InspectResult status;
    private Long repairRecordId;
    private String abnormalDesc;
    private String abnormalImages;
    private HandleStatus handleStatus;
    private String handleContent;
    private LocalDateTime handleTime;
    private Long handlerId;
    private String handlerName;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private String locationAddress;
    private String remark;
    private LocalDateTime createTime;
}
