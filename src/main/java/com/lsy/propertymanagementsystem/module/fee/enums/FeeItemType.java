package com.lsy.propertymanagementsystem.module.fee.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeeItemType {
    PROPERTY_FEE(1, "物业费"),
    PARKING_FEE(2, "车位费"),
    WATER_FEE(3, "水费"),
    ELECTRIC_FEE(4, "电费"),
    GAS_FEE(5, "燃气费"),
    HEATING_FEE(6, "暖气费"),
    OTHER(9, "其他");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static FeeItemType of(Integer value) {
        if (value == null) return null;
        for (FeeItemType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知收费项目类型: " + value);
    }
}