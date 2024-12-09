package com.visualization.runtime;

import java.time.Instant;

public class VLog {

    private VContext context;
    private String message;
    private VLogTheme theme;
    private Instant time;

    public VLog(VContext context, String message , VLogTheme theme) {
        this.context = context;
        this.message = message;
        this.theme = theme;
        this.time = Instant.now();
    }

    public VContext getContext() {
        return context;
    }

    public void setContext(VStageContext context) {
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContext(VContext context) {
        this.context = context;
    }

    public VLogTheme getTheme() {
        return theme;
    }

    public void setTheme(VLogTheme theme) {
        this.theme = theme;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "VLOG{" +
                "context=" + context +
                ", message='" + message + '\'' +
                ", theme=" + theme +
                ", time=" + time +
                '}';
    }
}
