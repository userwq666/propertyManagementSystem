package com.lsy.propertymanagementsystem.module.announcement.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnnouncementType {
    NOTICE(1, "通知公告"),
    POLICY(2, "政策法规"),
    SERVICE(3, "便民服务"),
    ACTIVITY(4, "活动通知"),
    URGENT(5, "紧急通知");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static AnnouncementType of(Integer value) {
        if (value == null) return null;
        for (AnnouncementType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知公告类型: " + value);
    }
}