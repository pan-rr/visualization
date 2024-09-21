package com.visualization.auth.enums;

public enum AccessType {

    DENY(-1),
    PERMIT(1);

    private final int type;

    AccessType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
