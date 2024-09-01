package com.visualization.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.AuthHolder;
import com.visualization.enums.ResponseEnum;
import com.visualization.fetch.AuthClient;
import com.visualization.model.Response;
import com.visualization.properties.WhiteList;
import com.visualization.util.PathUtil;
import com.visualization.util.ResponseUtil;
import com.visualization.auth.model.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CheckAccessFilter implements GlobalFilter, Ordered {

    @Resource
    private WhiteList whiteList;

    @Value("${visual.auth.token.name}")
    private String tokenName;

    @Resource(name = "tokenCache")
    private Cache<String, AuthHolder> cache;

    @Resource
    private WebClient.Builder builder;

    @Resource
    private AuthClient authClient;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        RequestPath path = request.getPath();
        String value = path.value();
        for (String pattern : whiteList.pattern) {
            if (PathUtil.match(pattern, value)) {
                return chain.filter(exchange);
            }
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
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(tokenName, authToken);
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
