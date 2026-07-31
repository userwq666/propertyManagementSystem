package com.lsy.propertymanagementsystem.module.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "类型不能为空")
    private Integer type;

    private String coverImage;
    private Integer publishStatus;

    private Integer isTop;
    private LocalDateTime topExpireTime;
    private LocalDateTime publishTime;
    private LocalDateTime scheduledPublishTime;
    private Long creatorId;
}
