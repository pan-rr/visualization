package com.visualization.auth;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AuthEvictionListener implements RemovalListener<String, AuthHolder> , ApplicationContextAware {

    private AuthHandler authHandler;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void onRemoval(@Nullable String string, @Nullable AuthHolder authHolder, @NonNull RemovalCause removalCause) {
        if (this.authHandler == null){
            this.authHandler = this.context.getBean(AuthHandler.class);
        }
        authHandler.vote(authHolder);
    }
}
