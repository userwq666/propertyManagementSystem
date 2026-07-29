package com.lsy.propertymanagementsystem.module.community.dto;

import com.lsy.propertymanagementsystem.module.community.enums.ParkingStatus;
import com.lsy.propertymanagementsystem.module.community.enums.ParkingType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CommunityParkingVO {
    private Long id;
    private String parkingNo;
    private ParkingType parkingType;
    private ParkingStatus status;
    private Long ownerId;
    private String ownerName;
    private BigDecimal rentPrice;
    private BigDecimal sellPrice;
    private String remark;
    private LocalDateTime createTime;
}
