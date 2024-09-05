package com.visualization.fetch;

import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class AuthMessageWatchDog {

    private static final Logger LOG = LoggerFactory.getLogger(AuthMessageWatchDog.class);


    @Value("${visual.auth.message.fetchInterval:10}")
    private Long fetchInterval;

    @Resource
    private AuthMessageConsumer consumer;

    @Resource
    private AuthClient authClient;

    private final AtomicReference<Long> timestamp = new AtomicReference<>(null);

    private final String id = UUID.randomUUID().toString();

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(fetchInterval);
                    fetchMessage();
                } catch (Throwable e) {
                    LOG.error("[watch dog]定时任务异常:{}", e.getMessage());
                }
            }
        });
    }

    public void fetchMessage() {
        AuthMessageRequest request = new AuthMessageRequest();
        request.timestamp = timestamp.get();
        request.consumerId = id;
        List<AuthMessage> list = authClient.fetchMessage(request)
                .doOnError((e) -> LOG.error("[watch dog]获取auth message 异常:{}", e.getMessage()))
                .collectList()
                .block();
        if (!CollectionUtils.isEmpty(list)) {
            long biggest = 0L;
            for (AuthMessage message : list) {
                if (biggest < message.messageTimestamp) biggest = message.messageTimestamp;
                consumer.consume(message);
            }
            timestamp.set(biggest);
        }

    }

}
