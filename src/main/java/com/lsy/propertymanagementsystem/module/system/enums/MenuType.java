package com.lsy.propertymanagementsystem.module.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuType {
    DIRECTORY(0, "目录"),
    MENU(1, "菜单"),
    BUTTON(2, "按钮");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static MenuType of(Integer value) {
        if (value == null) return null;
        for (MenuType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知菜单类型: " + value);
    }
}