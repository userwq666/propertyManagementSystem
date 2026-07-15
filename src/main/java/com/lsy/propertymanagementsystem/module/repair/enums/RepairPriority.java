package com.lsy.propertymanagementsystem.module.repair.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepairPriority {
    NORMAL(1, "普通"),
    URGENT(2, "加急"),
    EMERGENCY(3, "紧急");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static RepairPriority of(Integer value) {
        if (value == null) return null;
        for (RepairPriority e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知优先级: " + value);
    }
}