package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanStatus {
    DISABLED(0, "停用"),
    ENABLED(1, "启用");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static PlanStatus of(Integer value) {
        if (value == null) return null;
        for (PlanStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知计划状态: " + value);
    }
}
