package com.visualization.fetch;

import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageRequest;
import com.visualization.auth.model.AuthRequest;
import com.visualization.auth.model.AuthResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class AuthClient {

    @Resource
    private WebClient.Builder builder;

    public Mono<AuthResponse> checkToken(AuthRequest request, Map<String, List<String>> headers) {
        WebClient.RequestHeadersSpec<?> spec = builder.build()
                .post()
                .uri("http://VISUAL-AUTH/auth/api/checkToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request);
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((k, v) -> {
                spec.header(k, v.toArray(new String[0]));
            });
        }
        return spec.retrieve()
                .bodyToMono(AuthResponse.class);
    }

    public Flux<AuthMessage> fetchMessage(AuthMessageRequest request) {
        return builder.build()
                .post()
                .uri("http://VISUAL-AUTH/auth/api/fetchMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(AuthMessage.class);
    }
}
