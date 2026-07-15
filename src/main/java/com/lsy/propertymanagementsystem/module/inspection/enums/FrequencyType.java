package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FrequencyType {
    DAILY(1, "每日"),
    WEEKLY(2, "每周"),
    MONTHLY(3, "每月"),
    QUARTERLY(4, "每季度"),
    HALF_YEAR(5, "每半年"),
    YEARLY(6, "每年"),
    ONCE(7, "一次性");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static FrequencyType of(Integer value) {
        if (value == null) return null;
        for (FrequencyType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知频次类型: " + value);
    }
}