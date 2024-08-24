package com.visualization.enums;

import lombok.Getter;

@Getter
public enum UserTypeEnum {

    NORMAL(0),
    TENANT(1);
    private final int code;

    UserTypeEnum(int code) {
        this.code = code;
    }

    public static boolean isTenant(Integer code) {
        return TENANT.code == code;
    }
}
