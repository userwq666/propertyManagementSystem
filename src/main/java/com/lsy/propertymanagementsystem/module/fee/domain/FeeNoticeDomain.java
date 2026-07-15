package com.lsy.propertymanagementsystem.module.fee.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fee_notice")
public class FeeNoticeDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType;
    private Integer sendScope;
    private String buildingIds;
    private String ownerIds;
    private Integer sendStatus;
    private LocalDateTime sendTime;
    private Long creatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public void publish() {
        this.sendStatus = 1;
        this.sendTime = LocalDateTime.now();
    }
}
