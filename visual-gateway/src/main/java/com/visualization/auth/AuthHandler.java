package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageEnum;
import com.visualization.fetch.AuthMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class AuthHandler implements AuthMessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);

    @Resource(name = "tokenCache")
    private Cache<String, AuthHolder> cache;

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
            cache.put(build.getToken(), build);
        } else {
            holder.renewModifyTime();
        }
    }

    private void logout(AuthMessage message) {
        cache.invalidate(message.token);
    }
}
