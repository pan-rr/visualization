package com.visualization.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class AuthClientConfig {

    @Value("${visual.auth.permission.remain-max-count:500}")
    private Integer remainMaxCount;

    @Value("${visual.auth.permission.init-count:50}")
    private Integer initCount;

    @Bean(name = "permissionCache")
    public Cache<String, Boolean> tokenCache() {
        return Caffeine.newBuilder()
                .initialCapacity(initCount)
                .maximumSize(remainMaxCount)
                .expireAfterWrite(Duration.of(1, ChronoUnit.MINUTES))
                .build();
    }

    @Bean(name = "authRestTemplate")
    @LoadBalanced
    public RestTemplate authRestTemplate() {
        return new RestTemplate();
    }

}
