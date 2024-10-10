package com.visualization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean(name = "authRestTemplate")
    public RestTemplate authRestTemplate(){
        return new RestTemplate();
    }
}
