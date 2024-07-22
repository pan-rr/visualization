package com.visualisation.constant;

public class StatusConstant {

    public static final int NORMAL = 0;

    public static final int FINISHED = 1;
    public static final int BLOCK = -1;

    public static final int TERMINATE = -2;

    public static final int FORBIDDEN = -3;

    public static String getStatusName(int status) {
        switch (status) {
            case BLOCK:
                return "阻塞";
            case FINISHED:
                return "完成";
            case TERMINATE:
                return "终止";
            case FORBIDDEN:
                return "禁用";
            case NORMAL:
            default:
                return "正常";
        }
    }
}
