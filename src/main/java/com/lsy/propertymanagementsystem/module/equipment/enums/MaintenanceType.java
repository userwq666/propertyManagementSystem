package com.lsy.propertymanagementsystem.module.equipment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceType {
    DAILY_INSPECTION(1, "日常巡检"),
    REGULAR_MAINTENANCE(2, "定期保养"),
    FAULT_REPAIR(3, "故障维修"),
    PARTS_REPLACEMENT(4, "更换配件"),
    OTHER(5, "其他");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static MaintenanceType of(Integer value) {
        if (value == null) return null;
        for (MaintenanceType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知维护类型: " + value);
    }
}