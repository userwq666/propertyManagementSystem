package com.lsy.propertymanagementsystem.module.inspection.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.inspection.enums.HandleStatus;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import com.lsy.propertymanagementsystem.module.inspection.enums.TaskStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inspection_record")
public class InspectionRecordDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long equipmentId;
    private Long inspectorUserId;
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
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private String locationAddress;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void startInspect() {
        this.taskStatus = TaskStatus.PENDING;
        this.status = InspectResult.NOT_INSPECTED;
    }
    public void accept(Long inspectorUserId) {
        this.taskStatus = TaskStatus.ACCEPTED;
        this.inspectorUserId = inspectorUserId;
    }
    public void completeInspect() {
        this.taskStatus = TaskStatus.DONE;
    }
    public void normal() { this.status = InspectResult.NORMAL; }
    public void abnormal(String desc, String images) {
        this.status = InspectResult.ABNORMAL;
        this.abnormalDesc = desc;
        this.abnormalImages = images;
        this.handleStatus = HandleStatus.PENDING;
    }
    public void handle(String content, Long handlerId) {
        this.handleStatus = HandleStatus.HANDLED;
        this.handleContent = content;
        this.handleTime = LocalDateTime.now();
        this.handlerId = handlerId;
    }
}
