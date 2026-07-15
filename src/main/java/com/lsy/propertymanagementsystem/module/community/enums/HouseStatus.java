package com.lsy.propertymanagementsystem.module.community.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HouseStatus {
    VACANT(0, "空置"),
    OCCUPIED(1, "已入住"),
    RENTED(2, "出租");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static HouseStatus of(Integer value) {
        if (value == null) return null;
        for (HouseStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知房屋状态: " + value);
    }
}