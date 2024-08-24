package com.visualization.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    SUCCESS(0,"成功"),

    ERROR(-1,"错误");


    private final String message;
    private final Integer code;


    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
