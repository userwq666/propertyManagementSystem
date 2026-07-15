package com.lsy.propertymanagementsystem.module.announcement.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PublishStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "发布"),
    OFFLINE(2, "下架");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static PublishStatus of(Integer value) {
        if (value == null) return null;
        for (PublishStatus e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知发布状态: " + value);
    }
}