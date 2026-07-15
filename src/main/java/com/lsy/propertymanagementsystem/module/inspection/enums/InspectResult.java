package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InspectResult {
    NORMAL(1, "正常"),
    ABNORMAL(2, "异常"),
    NOT_INSPECTED(3, "未巡检");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static InspectResult of(Integer value) {
        if (value == null) return null;
        for (InspectResult e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知巡检结果: " + value);
    }
}