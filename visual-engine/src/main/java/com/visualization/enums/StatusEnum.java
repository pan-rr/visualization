package com.visualization.enums;

import com.visualization.exception.DAGException;
import com.visualization.model.portal.Option;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public enum StatusEnum {

    UNKNOWN(-999, "未知"),

    NORMAL(0, "正常"),

    FINISHED(1, "完成"),

    BLOCK(-1, "阻塞"),

    TERMINATE(-2, "终止"),

    FORBIDDEN(-3, "禁用"),

    NEW(-4, "待执行"),

    BLOCK_FAIL_REACH_THRESHOLD(-5, "阻塞，失败次数达到阈值"),

    HANG_BY_USER(-6, "用户挂起");


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

    public static boolean statusCanFinish(int status) {
        return NORMAL.status == status || FINISHED.status == status;
    }

    public static List<Option> getOptions(int type) {
        List<Option> res = new LinkedList<>();
        if (type == 1) {
            res.add(Option.builder().label(NORMAL.statusName).value(String.valueOf(NORMAL.status)).build());
            res.add(Option.builder().label(FORBIDDEN.statusName).value(String.valueOf(FORBIDDEN.status)).build());
        } else {
            for (StatusEnum value : values()) {
                res.add(Option.builder().label(value.statusName).value(String.valueOf(value.status)).build());
            }
        }
        return res;
    }
}
