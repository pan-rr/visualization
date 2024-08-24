package com.visualization.enums;

import com.visualization.exception.DAGException;
import lombok.Getter;

@Getter
public enum StatusEnum {

    UNKNOWN(-999, "未知"),

    NORMAL(0, "正常"),

    FINISHED(1, "完成"),

    BLOCK(-1, "阻塞"),

    TERMINATE(-2, "终止"),

    FORBIDDEN(-3, "禁用"),

    NEW(-4, "待执行");


    private final String statusName;
    private final Integer status;

    StatusEnum(Integer status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public static String getStatusNameByStatus(int status) {
        StatusEnum[] values = values();
        for (StatusEnum value : values) {
            if (value.status == status) return value.statusName;
        }
        return UNKNOWN.statusName;
    }

    public static void validateStatus(int status) {
        for (StatusEnum value : values()) {
            if (value.status == status) return;
        }
        throw new DAGException("未知状态编码：" + status);
    }

}
