package com.lsy.propertymanagementsystem.module.fee.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeeNoticeVO {
    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType;
    private Integer sendScope;
    private Integer sendStatus;
    private LocalDateTime sendTime;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
}
