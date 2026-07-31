package com.lsy.propertymanagementsystem.module.repair.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairPriority;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairStatus;
import com.lsy.propertymanagementsystem.module.repair.enums.RepairType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair_record")
public class RepairRecordDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String repairNo;
    private Long ownerId;
    private Long houseId;
    private RepairType repairType;
    private String repairContent;
    private String repairImages;
    private Long equipmentId;
    private RepairStatus status;
    private RepairPriority priority;
    private Long handlerId;
    private String handleContent;
    private String handleImages;
    private LocalDateTime handleTime;
    private Integer evaluateScore;
    private String evaluateContent;
    private LocalDateTime evaluateTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void prepareAdd() { this.status = RepairStatus.PENDING; this.priority = RepairPriority.NORMAL; }
    public void assignHandler(Long handlerId) { this.handlerId = handlerId; this.status = RepairStatus.PROCESSING; }
    public void complete(String handleContent, String handleImages) { this.status = RepairStatus.PENDING_EVALUATE; this.handleContent = handleContent; this.handleImages = handleImages; this.handleTime = LocalDateTime.now(); }
    public void evaluate(Integer score, String content) { this.evaluateScore = score; this.evaluateContent = content; this.evaluateTime = LocalDateTime.now(); this.status = RepairStatus.COMPLETED; }
    public void cancel() { this.status = RepairStatus.CANCELLED; }
}
