package com.huodongpai.common.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    ADMIN("admin", "管理员"),
    USER("user", "普通用户");

    private final String code;
    private final String description;

    UserRoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserRoleEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知角色编码: " + code));
    }
}
