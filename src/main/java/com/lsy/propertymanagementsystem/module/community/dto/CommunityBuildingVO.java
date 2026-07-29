package com.lsy.propertymanagementsystem.module.community.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityBuildingVO {
    private Long id;
    private String buildingNo;
    private Integer floorCount;
    private Integer totalHouse;
    private Integer buildYear;
    private String remark;
    private LocalDateTime createTime;
}
