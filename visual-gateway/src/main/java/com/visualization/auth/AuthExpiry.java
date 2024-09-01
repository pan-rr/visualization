package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;

public class AuthExpiry implements Expiry<String, AuthHolder> {
    @Override
    public long expireAfterCreate(String key, AuthHolder value, long currentTime) {
        return value.getTimeout();
    }

    @Override
    public long expireAfterUpdate(String key, AuthHolder value, long currentTime, @NonNegative long currentDuration) {
        return Long.MAX_VALUE;
    }

    @Override
    public long expireAfterRead(String key, AuthHolder value, long currentTime, @NonNegative long currentDuration) {
        return Long.MAX_VALUE;
    }
}
