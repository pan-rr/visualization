package com.visualization.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.visualization.auth.AuthExpiry;
import com.visualization.auth.AuthHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {

    @Value("${visual.auth.token.remain-max-count:5000}")
    private Integer remainMaxCount;

    @Value("${visual.auth.token.init-count:50}")
    private Integer initCount;


    @Bean(name = "tokenCache")
    public Cache<String, AuthHolder> tokenCache() {
        return Caffeine.newBuilder()
                .initialCapacity(initCount)
                .maximumSize(remainMaxCount)
                .expireAfter(new AuthExpiry())
                .build();
    }
}
