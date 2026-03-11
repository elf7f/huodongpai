package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SignupOperationTypeEnum {

    APPLY("apply", "提交报名"),
    CANCEL("cancel", "取消报名"),
    AUDIT_PASS("audit_pass", "审核通过"),
    AUDIT_REJECT("audit_reject", "审核驳回");

    private final String code;
    private final String description;

    SignupOperationTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SignupOperationTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知报名操作类型: " + code));
    }
}
