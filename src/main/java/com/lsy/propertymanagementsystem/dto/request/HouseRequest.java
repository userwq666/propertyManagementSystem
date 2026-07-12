package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HouseRequest {
    private Long id;
    
    @NotNull(message = "楼栋id不能为空")
    private Long buildingId;
    
    @NotBlank(message = "房间号不能为空")
    private String roomNo;
    
    private BigDecimal area;
    
    private String houseType;
    
    private Integer houseStatus;
    
    private Long ownerId;
    
    private String remark;
}