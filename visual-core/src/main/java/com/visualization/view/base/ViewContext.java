package com.visualization.view.base;

import java.util.Map;

public class ViewContext {
    private final Map<String, Object> context;

    public ViewContext(Map<String, Object> context) {
        this.context = context;
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }

    public void put(String key , Object value){
        context.put(key, value);
    }
}
