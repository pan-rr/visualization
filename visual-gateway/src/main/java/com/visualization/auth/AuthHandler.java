package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageEnum;
import com.visualization.fetch.AuthClient;
import com.visualization.fetch.AuthMessageConsumer;
import com.visualization.service.HeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AuthHandler implements AuthMessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);

    @Value("${spring.application.name}")
    private String serviceId;

    @Value("${visual.auth.token.activity-timeout:1800}")
    private Integer activityTimeout;

    @Resource(name = "tokenCache")
    private Cache<String, AuthHolder> cache;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource(name = "authRestTemplate")
    private RestTemplate restTemplate;

    @Resource
    private AuthClient authClient;

    @Resource
    private HeaderService headerService;

    private static final String VOTE_PATTERN = "http://{0}:{1}/inline/getModifyTime";

    @Override
    public void consume(AuthMessage message) {
        LOG.info("[auth handler]message:{}", message);
        AuthMessageEnum e = AuthMessageEnum.NOTHING;
        for (AuthMessageEnum value : AuthMessageEnum.values()) {
            if (value.getCode() == message.messageType) {
                e = value;
                break;
            }
        }
        switch (e) {
            case LOGIN:
                login(message);
                break;
            case LOGOUT:
                logout(message);
                break;
            default:
        }
    }

    private void login(AuthMessage message) {
        AuthHolder holder = cache.getIfPresent(message.token);
        if (Objects.isNull(holder)) {
            AuthHolder build = AuthHolder.build(message);
            build.setTimeout(Long.min(build.getTimeout(), activityTimeout * 10000));
            cache.put(build.getToken(), build);
        } else {
            holder.renewModifyTime();
        }
    }

    private void logout(AuthMessage message) {
        cache.invalidate(message.token);
        System.err.println("logout");
    }

    public Long getModifyTime(AuthHolder authHolder) {
        AuthHolder holder = cache.getIfPresent(authHolder.getToken());
        return holder == null ? -1L : holder.getModifyTime();
    }

    public void vote(AuthHolder authHolder) {
        long end = System.currentTimeMillis();
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (CollectionUtils.isEmpty(instances)) return;
        AtomicLong val = new AtomicLong(authHolder.getModifyTime());
        CompletableFuture.allOf(instances.stream().map(i -> {
            String path = MessageFormat.format(VOTE_PATTERN, i.getHost(), String.valueOf(i.getPort()));
            return CompletableFuture.runAsync(() -> {
                Long res = restTemplate.postForObject(path, authHolder, Long.class);
                if (res != null) {
                    val.accumulateAndGet(res, Math::max);
                }
            });
        }).toArray(CompletableFuture[]::new)).join();
        long max = val.get();
        Map<String, List<String>> header = headerService.buildAuthHeader(authHolder.getToken());
        if ((end - max) / 1000 > activityTimeout) {
            authClient.logout(header)
                    .subscribe((res) -> LOG.info("token logout"));
        } else {
            authClient.renewTimeout(header, activityTimeout.longValue())
                    .subscribe((res) -> LOG.info("token renew"));
        }
    }
}
