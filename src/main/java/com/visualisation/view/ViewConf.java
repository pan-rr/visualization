package com.visualisation.view;

import java.util.Map;

public class ViewConf {

    private Map<String,Object> properties;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ViewConf(Map<String, Object> properties) {
        this.properties = properties;
    }


}
