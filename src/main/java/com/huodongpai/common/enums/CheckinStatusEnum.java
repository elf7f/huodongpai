package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CheckinStatusEnum {

    NOT_CHECKED_IN(0, "未签到"),
    CHECKED_IN(1, "已签到");

    private final Integer code;
    private final String description;

    CheckinStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CheckinStatusEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知签到状态: " + code));
    }
}
