package com.lsy.propertymanagementsystem.module.community.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.lsy.propertymanagementsystem.module.community.enums.OwnerStatus;
import com.lsy.propertymanagementsystem.module.community.enums.OwnerType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_owner")
public class CommunityOwnerDomain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String idCard;
    private String idCardFront;
    private String idCardBack;
    private OwnerType ownerType;
    private OwnerStatus status;
    private String remark;
    private LocalDateTime checkInTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}