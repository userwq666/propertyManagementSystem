package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    PENDING(0, "待接单"),
    ACCEPTED(1, "已接单"),
    DONE(2, "已填写");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static TaskStatus of(Integer value) {
        if (value == null) return null;
        for (TaskStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知任务状态: " + value);
    }
}
