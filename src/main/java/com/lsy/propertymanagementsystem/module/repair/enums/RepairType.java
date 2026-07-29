package com.lsy.propertymanagementsystem.module.repair.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepairType {
    WATER_ELECTRICITY("水电", "水电"),
    DOOR_WINDOW("门窗", "门窗"),
    APPLIANCE("家电", "家电"),
    PUBLIC_FACILITY("公共设施", "公共设施"),
    OTHER("其他", "其他");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;

    public static RepairType of(String value) {
        if (value == null) return null;
        for (RepairType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知报修类型: " + value);
    }
}
