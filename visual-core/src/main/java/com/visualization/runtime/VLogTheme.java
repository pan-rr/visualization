package com.visualization.runtime;

public enum VLogTheme {

    START(0, "开始执行"),
    INFO(1, "信息"),
    FINISH(2, "完成"),
    FAIL(3, "异常");

    private final int code;
    private final String message;

    VLogTheme(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }
}
