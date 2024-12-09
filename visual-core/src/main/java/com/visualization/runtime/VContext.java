package com.visualization.runtime;

public interface VContext {

    <T> T get(String key, Class<T> clazz);

    void put(String key, Object value);

    VStageStatus getStatus();

    void setStatus(VStageStatus status);
}
