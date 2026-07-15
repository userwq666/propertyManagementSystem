package com.lsy.propertymanagementsystem.module.community.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CommunityParkingDTO {
    private Long id;

    @NotBlank(message = "车位编号不能为空")
    private String parkingNo;

    private Integer parkingType;

    private Integer status;

    private Long ownerId;

    private BigDecimal rentPrice;

    private BigDecimal sellPrice;

    private String remark;
}