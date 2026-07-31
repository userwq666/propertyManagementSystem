package com.lsy.propertymanagementsystem.module.inspection.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.inspection.enums.InspectResult;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inspection_record_log")
public class InspectionRecordLogDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long operatorId;
    private InspectResult beforeStatus;
    private InspectResult afterStatus;
    private String reason;
    private LocalDateTime createTime;
}
