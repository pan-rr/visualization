package com.visualization.filter;

import com.visualization.auth.PermissionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
public class CheckPermissionFilter implements GlobalFilter, Ordered {

    @Resource
    private PermissionHandler permissionHandler;

    @Override
    public int getOrder() {
        return 101;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return permissionHandler.checkPermission(exchange, chain);
    }
}
