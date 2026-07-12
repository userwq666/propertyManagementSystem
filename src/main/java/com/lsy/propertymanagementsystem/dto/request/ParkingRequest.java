package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ParkingRequest {
    private Long id;
    
    @NotBlank(message = "车位编号不能为空")
    private String parkingNo;
    
    private Integer parkingType;
    
    private Integer status;
    
    private Long ownerId;
    
    private LocalDateTime expireTime;
    
    private String remark;
}