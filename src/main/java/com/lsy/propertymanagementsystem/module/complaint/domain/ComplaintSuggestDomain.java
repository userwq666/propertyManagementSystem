package com.lsy.propertymanagementsystem.module.complaint.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintPriority;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintStatus;
import com.lsy.propertymanagementsystem.module.complaint.enums.ComplaintType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("complaint_suggest")
public class ComplaintSuggestDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String complaintNo;
    private Long ownerId;
    private Long creatorId;
    private Long houseId;
    private ComplaintType type;
    private String category;
    private String content;
    private String images;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private Long handlerId;
    private String handleContent;
    private LocalDateTime handleTime;
    private Integer evaluateScore;
    private String evaluateContent;
    private LocalDateTime evaluateTime;
    private Integer isAnonymous;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public void prepareAdd() { this.status = ComplaintStatus.PENDING; this.priority = ComplaintPriority.NORMAL; this.isAnonymous = 0; }
    public void assignHandler(Long handlerId) { this.handlerId = handlerId; this.status = ComplaintStatus.ACCEPTED; }
    public void reply(String handleContent) { this.status = ComplaintStatus.REPLIED; this.handleContent = handleContent; this.handleTime = LocalDateTime.now(); }
    public void confirm() { this.status = ComplaintStatus.COMPLETED; }
    public void evaluate(Integer score, String content) {
        this.evaluateScore = score;
        this.evaluateContent = content;
        this.evaluateTime = LocalDateTime.now();
        this.status = ComplaintStatus.COMPLETED;
    }
}
