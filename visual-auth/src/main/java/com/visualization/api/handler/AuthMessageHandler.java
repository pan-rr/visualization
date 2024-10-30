package com.visualization.api.handler;

import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageType;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
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

    public void publish(AuthMessage message) {
        List<ServiceInstance> instances = discoveryClient.getInstances("VISUAL-GATEWAY");
        if (!CollectionUtils.isEmpty(instances)) {
            CompletableFuture.allOf(instances.stream().map(instance -> {
                String path = MessageFormat.format(AUTH_FEEDBACK_PATTERN, instance.getHost(), String.valueOf(instance.getPort()));
                return CompletableFuture.runAsync(() -> {
                    restTemplate.postForObject(path, message, Object.class);
                }).exceptionally(throwable -> null);
            }).toArray(CompletableFuture[]::new)).join();
        }
    }

    public void publishLoginMessage(String token, Long timestamp) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageType.LOGIN.getCode();
        message.token = token;
        message.timeout = timestamp;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }

    public void publishLogoutMessage(String token) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageType.LOGOUT.getCode();
        message.token = token;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }

}
