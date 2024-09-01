package com.visualization.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    SUCCESS(0, "成功"),

    ERROR(-1, "错误"),

    NOT_ACCESS(-999, "未登录"),

    ACCESS_EXPIRE(-998, "登录凭证过期"),

    ACCESS_DENY(-997, "权限不足");


    private final String message;
    private final Integer code;


    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
