package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

    @Bean("authRestTemplate")
    public RestTemplate authRestTemplate() {
        return new RestTemplate();
    }
}
