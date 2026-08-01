package com.lsy.propertymanagementsystem.module.fee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FeeGenerateDTO {
    @NotNull(message = "收费项目不能为空")
    private Long itemId;

    @NotNull(message = "请选择房屋")
    private List<Long> houseIds;
}
