package com.lsy.propertymanagementsystem.module.community.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParkingStatus {
    FREE(0, "空闲"),
    RENTED(1, "已租"),
    SOLD(2, "已售"),
    MAINTENANCE(3, "维修中");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static ParkingStatus of(Integer value) {
        if (value == null) return null;
        for (ParkingStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知车位状态: " + value);
    }
}