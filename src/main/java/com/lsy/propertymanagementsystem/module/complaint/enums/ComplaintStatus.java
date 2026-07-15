package com.lsy.propertymanagementsystem.module.complaint.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplaintStatus {
    PENDING(0, "待受理"),
    ACCEPTED(1, "已受理"),
    PROCESSING(2, "处理中"),
    REPLIED(3, "已回复"),
    CLOSED(4, "已关闭"),
    CANCELLED(5, "已撤销");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static ComplaintStatus of(Integer value) {
        if (value == null) return null;
        for (ComplaintStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知投诉状态: " + value);
    }
}