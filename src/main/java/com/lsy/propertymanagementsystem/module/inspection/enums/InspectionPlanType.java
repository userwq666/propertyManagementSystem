package com.lsy.propertymanagementsystem.module.inspection.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InspectionPlanType {
    DAILY(1, "日常巡检"),
    SPECIAL(2, "专项巡检"),
    SEASONAL(3, "季节性巡检"),
    TEMPORARY(4, "临时巡检");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static InspectionPlanType of(Integer value) {
        if (value == null) return null;
        for (InspectionPlanType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知巡检计划类型: " + value);
    }
}