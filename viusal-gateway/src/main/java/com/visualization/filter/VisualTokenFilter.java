package com.visualization.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Order(value = -1)
public class VisualTokenFilter implements GlobalFilter {

    @Value("${visual.token-name}")
    private String tokenName;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> list = new ArrayList<>();
        HttpHeaders headers = request.getHeaders();
        Set<String> set = headers.keySet();
        String tokenNameUpperCase = tokenName.toUpperCase();
        for (String s : set) {
            if (tokenNameUpperCase.equals(s.toUpperCase())) {
                list = headers.getOrDefault(s, new ArrayList<>());
            }
        }
        ServerHttpRequest newRequest = request
                .mutate()
                .header(tokenName, list.toArray(list.toArray(new String[0])))
                .build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);
    }
}
