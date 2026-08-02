package com.lsy.propertymanagementsystem.module.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplaintSuggestDTO {
    private Long id;

    private Long ownerId;

    private Long houseId;

    private Integer type;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String images;

    private Integer status;

    private Integer priority;

    private Long handlerId;

    private String handleContent;

    private Integer isAnonymous;

    private Integer evaluateScore;

    private String evaluateContent;
}
