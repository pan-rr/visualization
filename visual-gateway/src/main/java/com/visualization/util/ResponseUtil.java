package com.visualization.util;

import com.google.gson.Gson;
import com.visualization.model.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ALREADY_ROUTED_ATTR;

public class ResponseUtil {

    public static Mono<Void> writeResponse(ServerWebExchange exchange, Response<?> data) {
        ServerHttpResponse response = exchange.getResponse();
        exchange.getAttributes().put(GATEWAY_ALREADY_ROUTED_ATTR, true);
        Gson gson = new Gson();
        String json = gson.toJson(data);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(buffer));
    }
}
