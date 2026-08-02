package com.lsy.propertymanagementsystem.module.complaint.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplaintType {
    COMPLAINT(1, "投诉"),
    SUGGESTION(2, "建议"),
    INQUIRY(3, "咨询");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static ComplaintType of(Integer value) {
        if (value == null) return null;
        for (ComplaintType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知投诉类型: " + value);
    }
}
