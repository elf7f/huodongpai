package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum EventRuntimeStatusEnum {

    DRAFT("draft", "草稿"),
    CANCELLED("cancelled", "已取消"),
    SIGNUP_OPEN("signup_open", "报名中"),
    SIGNUP_CLOSED("signup_closed", "报名结束"),
    ONGOING("ongoing", "进行中"),
    FINISHED("finished", "已结束");

    private final String code;
    private final String description;

    EventRuntimeStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EventRuntimeStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知活动运行状态: " + code));
    }
}
