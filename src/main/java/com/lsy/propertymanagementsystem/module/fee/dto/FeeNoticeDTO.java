package com.lsy.propertymanagementsystem.module.fee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FeeNoticeDTO {
    private Long id;

    @NotBlank(message = "通知标题不能为空")
    private String noticeTitle;

    private String noticeContent;

    @NotNull(message = "通知类型不能为空")
    private Integer noticeType;

    @NotNull(message = "发送范围不能为空")
    private Integer sendScope;

    private Integer sendStatus;
    private Long itemId;
    private Long creatorId;
    private List<Long> buildingIds;
}
