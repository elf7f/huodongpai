package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EventBaseStatusEnum {

    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String description;

    EventBaseStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EventBaseStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知活动基础状态: " + code));
    }
}
