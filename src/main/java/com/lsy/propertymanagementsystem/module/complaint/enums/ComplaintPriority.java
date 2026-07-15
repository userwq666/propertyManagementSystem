package com.lsy.propertymanagementsystem.module.complaint.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplaintPriority {
    NORMAL(1, "普通"),
    IMPORTANT(2, "重要"),
    URGENT(3, "紧急");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static ComplaintPriority of(Integer value) {
        if (value == null) return null;
        for (ComplaintPriority e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知优先级: " + value);
    }
}