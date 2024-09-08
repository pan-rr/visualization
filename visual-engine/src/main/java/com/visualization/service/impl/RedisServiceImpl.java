package com.visualization.service.impl;

import com.visualization.service.RedisService;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ServerProperties serverProperties;

    @Override
    public boolean lock(String key, Duration duration) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String address = serverProperties.getAddress().getHostAddress();
        return ops.setIfAbsent(key, address, duration);
    }

    @Override
    public boolean unlock(String key) {
        return redisTemplate.delete(key);
    }
}
