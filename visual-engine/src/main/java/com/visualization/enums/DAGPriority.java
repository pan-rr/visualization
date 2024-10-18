package com.visualization.enums;

import com.visualization.exception.DAGException;

public enum DAGPriority {
    MAX(10),
    MIN(1);

    private final double priority;

    DAGPriority(double priority) {
        this.priority = priority;
    }

    public static void validatePriority(double priority) {
        if (Double.compare(MAX.priority, priority) < 0 || Double.compare(priority, MIN.priority) < 0) {
            throw new DAGException("优先级只能1-10");
        }
    }
}
