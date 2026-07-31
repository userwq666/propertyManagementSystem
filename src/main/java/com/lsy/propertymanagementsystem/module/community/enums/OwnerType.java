package com.lsy.propertymanagementsystem.module.community.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerType {
    SELF(1, "本人"),
    FAMILY(2, "家属"),
    TENANT(3, "租客");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    @JsonCreator
    public static OwnerType of(Integer value) {
        if (value == null) return null;
        for (OwnerType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知业主类型: " + value);
    }
}