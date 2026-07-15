package com.lsy.propertymanagementsystem.module.community.dto;

import com.lsy.propertymanagementsystem.module.community.enums.OwnerStatus;
import com.lsy.propertymanagementsystem.module.community.enums.OwnerType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommunityOwnerDTO {
    private Long id;

    private Long userId;

    @NotBlank(message = "业主姓名不能为空")
    private String name;

    private String phone;

    private String idCard;

    private String idCardFront;

    private String idCardBack;

    private OwnerType ownerType;

    private OwnerStatus status;

    private String remark;

    private LocalDateTime checkInTime;
}