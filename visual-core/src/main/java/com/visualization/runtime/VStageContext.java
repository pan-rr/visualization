package com.visualization.runtime;

import java.util.Map;

public class VStageContext implements VContext{

    private final Map<String, Object> context;

    private VStageStatus status;

    public VStageContext(Map<String, Object> context) {
        this.context = context;
        this.status = VStageStatus.INIT;
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }

    public void put(String key, Object value) {
        context.put(key, value);
    }

    public VStageStatus getStatus() {
        return status;
    }

    public void setStatus(VStageStatus status) {
        this.status = status;
    }

}
