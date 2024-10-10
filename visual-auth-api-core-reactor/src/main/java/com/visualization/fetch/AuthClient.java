package com.visualization.fetch;

import com.visualization.auth.model.AuthRequest;
import com.visualization.auth.model.AuthResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class AuthClient {

    @Resource
    private WebClient.Builder builder;

    private void setHeaders(WebClient.RequestHeadersSpec<?> spec, Map<String, List<String>> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((k, v) -> {
                spec.header(k, v.toArray(new String[0]));
            });
        }
    }

    public Mono<AuthResponse> checkToken(AuthRequest request, Map<String, List<String>> headers) {
        WebClient.RequestHeadersSpec<?> spec = builder.build()
                .post()
                .uri("http://VISUAL-AUTH/auth/api/checkToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request);
        this.setHeaders(spec, headers);
        return spec.retrieve().bodyToMono(AuthResponse.class);
    }


    public Mono<Void> renewTimeout(Map<String, List<String>> headers, Long timeout) {
        WebClient.RequestHeadersSpec<?> spec = builder.build()
                .get()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .host("VISUAL-AUTH")
                        .path("/auth/api/renewTimeout")
                        .queryParam("timeout",timeout)
                        .build())
                .accept(MediaType.APPLICATION_JSON);
        this.setHeaders(spec, headers);
        return spec.retrieve().bodyToMono(Void.class);
    }

    public Mono<Void> logout(Map<String, List<String>> headers) {
        WebClient.RequestHeadersSpec<?> spec = builder.build()
                .get()
                .uri("http://VISUAL-AUTH/auth/api/logout")
                .accept(MediaType.APPLICATION_JSON);
        this.setHeaders(spec, headers);
        return spec.retrieve().bodyToMono(Void.class);
    }
}
