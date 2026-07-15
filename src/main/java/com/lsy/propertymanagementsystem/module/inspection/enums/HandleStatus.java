package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HandleStatus {
    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    HANDLED(2, "已处理"),
    IGNORED(3, "忽略");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static HandleStatus of(Integer value) {
        if (value == null) return null;
        for (HandleStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知处理状态: " + value);
    }
}