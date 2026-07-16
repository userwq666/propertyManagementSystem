package com.lsy.propertymanagementsystem.module.repair.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepairStatus {
    PENDING(0, "待派单"),
    PROCESSING(1, "处理中"),
    PENDING_EVALUATE(2, "待评价"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static RepairStatus of(Integer value) {
        if (value == null) return null;
        for (RepairStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知报修状态: " + value);
    }
}
