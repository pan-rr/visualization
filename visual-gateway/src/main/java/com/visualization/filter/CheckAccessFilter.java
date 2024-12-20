package com.visualization.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.AuthHolder;
import com.visualization.auth.model.AuthRequest;
import com.visualization.enums.ResponseEnum;
import com.visualization.fetch.AuthClient;
import com.visualization.model.Response;
import com.visualization.auth.ResourceConfig;
import com.visualization.service.HeaderService;
import com.visualization.util.PathUtil;
import com.visualization.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CheckAccessFilter implements GlobalFilter, Ordered {

    @Resource
    private ResourceConfig resourceConfig;

    @Value("${visual.auth.token.auth-name}")
    private String tokenName;

    @Resource(name = "tokenCache")
    private Cache<String, AuthHolder> cache;

    @Value("${visual.auth.token.activity-timeout:1800}")
    private Integer activityTimeout;

    @Resource
    private WebClient.Builder builder;

    @Resource
    private AuthClient authClient;

    @Resource
    private HeaderService headerService;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (PathUtil.anyMatch(resourceConfig.accessWhiteList,request.getPath())){
            return chain.filter(exchange);
        }
        List<String> authToken = request.getHeaders().get(tokenName);
        if (CollectionUtils.isEmpty(authToken)) {
            return ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.NOT_ACCESS));
        } else {
            AuthHolder holder;
            for (String s : authToken) {
                holder = cache.getIfPresent(s);
                if (Objects.nonNull(holder)) {
                    holder.renewModifyTime();
                    return chain.filter(exchange);
                }
            }
        }
        AuthRequest req = new AuthRequest();
        req.setTimeout(Long.valueOf(activityTimeout));
        Map<String, List<String>> headers = headerService.buildAuthHeader(authToken);
        return authClient.checkToken(req, headers)
                .flatMap((res) -> {
                    if (res.isTokenLegal()) {
                        return chain.filter(exchange);
                    } else {
                        return ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.NOT_ACCESS));
                    }
                });
    }
}
