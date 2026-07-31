package com.lsy.propertymanagementsystem.module.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "正常");

    @EnumValue
    @JsonValue
    private final Integer value;
    // 用户状态描述
    private final String desc;

    // 根据值获取枚举
    public static UserStatus of(Integer value) {
        if (value == null) return null;
        for (UserStatus e : values()) {              // 遍历所有枚举值
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知用户状态: " + value); // 如果没有匹配的枚举值，抛出异常
    }
}