package com.lsy.propertymanagementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RepairRecordRequest {
    private Long id;

    @NotNull(message = "业主id不能为空")
    private Long ownerId;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;

    @NotBlank(message = "报修类型不能为空")
    private String repairType;

    @NotBlank(message = "故障描述不能为空")
    private String content;

    private String imgUrl;

    private Integer status;

    private String handleUser;

    private String handleResult;

    private Integer rating;
}
