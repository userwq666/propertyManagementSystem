package com.lsy.propertymanagementsystem.module.equipment.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentStatus {
    NORMAL(1, "正常"),
    FAULT(2, "故障"),
    UNDER_REPAIR(3, "维修中"),
    DISABLED(4, "停用"),
    SCRAPPED(5, "报废");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static EquipmentStatus of(Integer value) {
        if (value == null) return null;
        for (EquipmentStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知设备状态: " + value);
    }
}