package com.visualization.enums;

import com.visualization.exception.DAGException;

public enum DAGPriorityEnum {
    MAX(10),
    MIN(1);

    private final double priority;

    DAGPriorityEnum(double priority) {
        this.priority = priority;
    }

    public static void validatePriority(double priority) {
        if (Double.compare(MAX.priority, priority) < 0 || Double.compare(priority, MIN.priority) < 0) {
            throw new DAGException("优先级只能1-10");
        }
    }
}
