package com.lsy.propertymanagementsystem.module.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    SUPER_ADMIN(1, "超级管理员"),
    PROPERTY_ADMIN(2, "物业管理员"),
    OWNER(3, "业主"),
    REPAIR_WORKER(4, "维修工"),
    INSPECTOR(5, "巡检员");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static UserType of(Integer value) {
        if (value == null) return null;
        for (UserType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知用户类型: " + value);
    }
}
