package com.visualization.api.handler;

import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageEnum;
import com.visualization.auth.message.AuthMessageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class AuthMessageHandler {

    private final ConcurrentSkipListSet<AuthMessage> set = new ConcurrentSkipListSet<>();

    private final ConcurrentHashMap<String, Long> vote = new ConcurrentHashMap<>();

    private AuthMessage buildQuery(long timestamp) {
        AuthMessage message = new AuthMessage();
        message.messageTimestamp = timestamp;
        return message;
    }

    private void delete(Long timestamp) {
        if (set.isEmpty()) return;
        if (timestamp < set.first().messageTimestamp)return;
        AuthMessage query = buildQuery(timestamp);
        NavigableSet<AuthMessage> remove = set.headSet(query, true);
        set.removeAll(remove);
    }

    public void publish(AuthMessage message) {
        set.add(message);
    }

    public void publishLoginMessage(String token, Long timestamp) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageEnum.LOGIN.getCode();
        message.token = token;
        message.timeout = timestamp;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }

    public void publishLogoutMessage(String token) {
        AuthMessage message = new AuthMessage();
        message.messageType = AuthMessageEnum.LOGOUT.getCode();
        message.token = token;
        message.messageTimestamp = System.currentTimeMillis();
        publish(message);
    }
    private List<AuthMessage> consume() {
        AuthMessage query = buildQuery(System.currentTimeMillis());
        return new ArrayList<>(set.headSet(query));
    }

    private void deleteMessage(AuthMessageRequest request) {
        if (Objects.isNull(request.timestamp)) return;
        vote.put(request.consumerId, request.timestamp);
        Collection<Long> values = vote.values();
        long time = Long.MAX_VALUE;
        for (Long value : values) {
            if (value < time) time = value;
        }
        delete(time);
    }


    public List<AuthMessage> fetchMessage(AuthMessageRequest request) {
        deleteMessage(request);
        return consume();
    }

}
