package com.lsy.propertymanagementsystem.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class OwnerRequest {
    private Long id;
    
    private Long userId;
    
    @NotBlank(message = "业主姓名不能为空")
    private String name;
    
    private String idCard;
    
    private String phone;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    private LocalDateTime checkInTime;
}