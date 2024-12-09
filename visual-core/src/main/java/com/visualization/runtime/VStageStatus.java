package com.visualization.runtime;

public enum VStageStatus {

    INIT(-1),
    FINISHED(0),
    WAITING_SUBTASK(1),
    SUBTASK_FINISH(2),
    FOUND_EXCEPTION(3);

    private final int code;

    VStageStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
