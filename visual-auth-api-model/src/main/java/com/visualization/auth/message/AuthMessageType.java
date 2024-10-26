package com.visualization.auth.message;

public enum AuthMessageType {

    NOTHING(0),

    LOGIN(1),

    PERMISSION_CHANGE(2),

    ROLE_CHANGE(3),

    LOGOUT(-1);


    private final int code;

    AuthMessageType(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean equalsGivenCode(int code){
        return this.code == code;
    }
}
