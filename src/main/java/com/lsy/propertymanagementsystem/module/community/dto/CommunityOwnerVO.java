package com.lsy.propertymanagementsystem.module.community.dto;

import com.lsy.propertymanagementsystem.module.community.enums.OwnerStatus;
import com.lsy.propertymanagementsystem.module.community.enums.OwnerType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityOwnerVO {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String phone;
    private String idCard;
    private String idCardFront;
    private String idCardBack;
    private OwnerType ownerType;
    private OwnerStatus status;
    private String remark;
    private LocalDateTime createTime;
}
