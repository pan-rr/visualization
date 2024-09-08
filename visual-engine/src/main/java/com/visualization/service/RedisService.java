package com.visualization.service;

import java.time.Duration;

public interface RedisService {

    boolean lock(String key, Duration duration);

    boolean unlock(String key);
}
