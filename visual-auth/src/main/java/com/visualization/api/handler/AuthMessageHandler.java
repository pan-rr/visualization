package com.visualization.api.handler;

import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageTypeEnum;
import com.visualization.utils.ShortLinkUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AuthMessageHandler {

    @Resource(name = "authRestTemplate")
    private RestTemplate restTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private DiscoveryClient discoveryClient;

    private static final String AUTH_FEEDBACK_PATTERN = "http://{0}:{1}/inline/messageFeedback";

    public String cacheKey(String token){
        return "visual:auth:stat:" + ShortLinkUtil.zip(token);
    }

    private boolean publishable(AuthMessage message) {
        String key = cacheKey(message.token);
        if (AuthMessageTypeEnum.LOGOUT.equalsGivenCode(message.messageType)){
            redisTemplate.delete(key);
            return true;
        }
        return redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(System.currentTimeMillis()), Duration.ofMinutes(30));
    }

    public void publish(AuthMessage message) {
        if (!publishable(message)) {
            return;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("VISUAL-GATEWAY");
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }
        CompletableFuture.allOf(instances.stream().map(instance -> {
            String path = MessageFormat.format(AUTH_FEEDBACK_PATTERN, instance.getHost(), String.valueOf(instance.getPort()));
            return CompletableFuture.runAsync(() -> {
                restTemplate.postForObject(path, message, Object.class);
            }).exceptionally(throwable -> null);
        }).toArray(CompletableFuture[]::new)).join();
    }

    public void publishLoginMessage(String token, Long timestamp) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageTypeEnum.LOGIN.getCode();
        message.token = token;
        message.timeout = timestamp;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }

    public void publishLogoutMessage(String token) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageTypeEnum.LOGOUT.getCode();
        message.token = token;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }

}
