package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class AuthConfig {

    @Value("${visual.auth.token.remain-max-count:5000}")
    private Integer remainMaxCount;

    @Value("${visual.auth.token.init-count:50}")
    private Integer initCount;


    @Bean(name = "tokenCache")
    public Cache<String, AuthHolder> tokenCache(@Autowired AuthEvictionListener authEvictionListener) {
        return Caffeine.newBuilder()
                .initialCapacity(initCount)
                .maximumSize(remainMaxCount)
                .expireAfter(new AuthExpiry())
                .evictionListener(authEvictionListener)
                .build();
    }

    @Bean(name = "permissionCache")
    public Cache<String, Boolean> permissionCache() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Bean("authRestTemplate")
    public RestTemplate authRestTemplate() {
        return new RestTemplate();
    }
}
