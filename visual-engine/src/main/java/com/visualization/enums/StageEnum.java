package com.visualization.enums;

import lombok.Getter;

@Getter
public enum StageEnum {


    STAGE_UNKNOWN("未知状态"),
    STAGE_START("开始执行。。。"),
    STAGE_SUCCESS("执行成功！"),
    STAGE_FAIL("执行失败");
    private final String message;

    StageEnum(String message) {
        this.message = message;
    }

}
