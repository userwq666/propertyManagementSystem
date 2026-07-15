package com.lsy.propertymanagementsystem.module.fee.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeeCycleType {
    MONTHLY(1, "按月"),
    QUARTERLY(2, "按季"),
    HALF_YEAR(3, "按半年"),
    YEARLY(4, "按年"),
    ONCE(5, "一次性");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static FeeCycleType of(Integer value) {
        if (value == null) return null;
        for (FeeCycleType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知收费周期: " + value);
    }
}