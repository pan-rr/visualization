package com.visualization.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "visual.gateway.white-list")
public class WhiteList {

    public List<String> pattern;

    public List<String> getPattern() {
        return pattern;
    }

    public void setPattern(List<String> pattern) {
        this.pattern = pattern;
    }
}
