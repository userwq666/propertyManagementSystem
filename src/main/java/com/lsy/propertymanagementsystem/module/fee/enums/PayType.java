package com.lsy.propertymanagementsystem.module.fee.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayType {
    CASH(1, "现金"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝"),
    BANK_CARD(4, "银行卡"),
    TRANSFER(5, "转账");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;

    public static PayType of(Integer value) {
        if (value == null) return null;
        for (PayType e : values()) {
            if (e.value.equals(value)) return e;
        }
        throw new IllegalArgumentException("未知支付方式: " + value);
    }
}