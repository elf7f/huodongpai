package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SignupStatusEnum {

    PENDING("pending", "待审核"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已驳回"),
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String description;

    SignupStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SignupStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知报名状态: " + code));
    }
}
