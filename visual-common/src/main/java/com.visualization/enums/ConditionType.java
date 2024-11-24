package com.visualization.enums;

public enum ConditionType {
    unknown,
    eq,
    in,
    gt,
    lt,
    gte,
    lte;

    public static ConditionType getByName(String name) {
        for (ConditionType value : ConditionType.values()) {
            if (value.name().equals(name)) return value;
        }
        return unknown;
    }
}
