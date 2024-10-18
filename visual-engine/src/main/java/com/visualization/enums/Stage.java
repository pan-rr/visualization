package com.visualization.enums;

import lombok.Getter;

@Getter
public enum Stage {


    STAGE_UNKNOWN("未知状态"),
    STAGE_START("开始执行。。。"),
    STAGE_FINISHED("执行完成！"),
    STAGE_FAIL("执行失败！");

    private final String message;

    Stage(String message) {
        this.message = message;
    }

}
