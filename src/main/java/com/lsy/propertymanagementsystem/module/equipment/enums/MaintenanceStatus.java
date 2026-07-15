package com.lsy.propertymanagementsystem.module.equipment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceStatus {
    PENDING(0, "待维护"),
    IN_PROGRESS(1, "进行中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "取消");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static MaintenanceStatus of(Integer value) {
        if (value == null) return null;
        for (MaintenanceStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知维护状态: " + value);
    }
}