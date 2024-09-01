package com.visualization.auth;

import com.visualization.fetch.AuthMessageWatchDog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public AuthMessageWatchDog authMessageWatchDog(){
        return new AuthMessageWatchDog();
    }
}
