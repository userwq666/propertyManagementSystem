package com.lsy.propertymanagementsystem.module.community.dto;

import com.lsy.propertymanagementsystem.module.community.enums.HouseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommunityHouseVO {
    private Long id;
    private Long buildingId;
    private String buildingNo;
    private String roomNo;
    private BigDecimal area;
    private String houseType;
    private HouseStatus houseStatus;
    private Long ownerId;
    private String ownerName;
    private String remark;
    private LocalDateTime createTime;
}
