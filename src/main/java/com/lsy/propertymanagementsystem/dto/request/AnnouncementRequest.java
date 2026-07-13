package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementRequest {
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "类型不能为空")
    private String type;

    private Integer status;
    private Integer isTop;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
}
