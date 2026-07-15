package com.lsy.propertymanagementsystem.module.community.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParkingType {
    ABOVE_GROUND(1, "地上"),
    UNDERGROUND(2, "地下");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static ParkingType of(Integer value) {
        if (value == null) return null;
        for (ParkingType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知车位类型: " + value);
    }
}