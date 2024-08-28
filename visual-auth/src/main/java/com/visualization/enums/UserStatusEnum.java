package com.visualization.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {

    NORMAL(1),
    FORBIDDEN(-1);

    private final int code;

    UserStatusEnum(int code) {
        this.code = code;
    }
}
