package com.lsy.propertymanagementsystem.module.fee.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeeRecordStatus {
    UNPAID(0, "未缴费"),
    PARTIAL_PAID(1, "部分缴费"),
    PAID(2, "已缴费"),
    OVERDUE(3, "逾期"),
    CANCELLED(4, "作废");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static FeeRecordStatus of(Integer value) {
        if (value == null) return null;
        for (FeeRecordStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知缴费状态: " + value);
    }
}