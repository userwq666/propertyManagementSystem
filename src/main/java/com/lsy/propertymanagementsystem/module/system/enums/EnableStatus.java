package com.lsy.propertymanagementsystem.module.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnableStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static EnableStatus of(Integer value) {
        if (value == null) return null;
        for (EnableStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知启用状态: " + value);
    }
}