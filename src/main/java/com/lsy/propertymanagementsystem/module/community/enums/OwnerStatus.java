package com.lsy.propertymanagementsystem.module.community.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "正常");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    @JsonCreator
    public static OwnerStatus of(Integer value) {
        if (value == null) return null;
        for (OwnerStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知业主状态: " + value);
    }
}