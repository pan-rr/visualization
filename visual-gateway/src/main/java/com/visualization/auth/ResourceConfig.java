package com.visualization.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "visual.auth.resource-config")
@Data
public class ResourceConfig {

    public List<String> accessWhiteList;

    public List<String> resourceWhiteList;

    public List<AuthResource> resources;

}
