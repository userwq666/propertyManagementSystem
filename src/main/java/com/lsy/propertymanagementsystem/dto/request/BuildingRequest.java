package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class BuildingRequest {
    private Long id;
    
    @NotBlank(message = "楼栋编号不能为空")
    private String buildingNo;
    
    private Integer floorCount;
    
    private Integer totalHouse;
    
    private Integer buildYear;
    
    private String remark;
}