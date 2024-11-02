package com.visualization.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.model.AuthRequest;
import com.visualization.enums.ResponseEnum;
import com.visualization.fetch.AuthClient;
import com.visualization.model.Response;
import com.visualization.service.HeaderService;
import com.visualization.util.HeaderUtil;
import com.visualization.util.PathUtil;
import com.visualization.util.ResponseUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Objects;

@Service
public class PermissionHandler {

    @Value("${visual.auth.token.resource-name}")
    private String resourceName;

    @Value("${visual.auth.token.tenant-name}")
    private String tenantName;

    @Value("${visual.auth.token.auth-name}")
    private String authName;

    @Resource(name = "permissionCache")
    private Cache<String, Boolean> permissionCache;

    @Resource
    private ResourceConfig resourceConfig;

    @Resource
    private HeaderService headerService;

    @Resource
    private AuthClient authClient;

    public Mono<Void> checkPermission(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (PathUtil.anyMatch(resourceConfig.resourceWhiteList, request.getPath())) return chain.filter(exchange);
        AuthResource resource = null;
        String path = request.getPath().value();
        for (AuthResource authResource : resourceConfig.resources) {
            if (PathUtil.match(authResource.getPattern(), path)) {
                resource = authResource;
                break;
            }
        }
        HttpHeaders headers = request.getHeaders();
        String authHeader = HeaderUtil.getOneHeader(headers, authName);
        String resourceHeader = HeaderUtil.getOneHeader(headers, resourceName);
        if (Objects.isNull(resourceHeader) || Objects.isNull(resource)) {
            return ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.ACCESS_DENY));
        }
        Boolean ifPresent = permissionCache.getIfPresent(resourceHeader);
        if (Objects.nonNull(ifPresent)) {
            return Boolean.TRUE.equals(ifPresent) ? chain.filter(exchange) : ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.ACCESS_DENY));
        }
        String code = URLEncoder.encode(resource.getResource());
        String currentTenant = HeaderUtil.getOneHeader(headers, tenantName);
        String encode = DigestUtils.md5Hex(DigestUtils.md5Hex(code + path + currentTenant) + authHeader);
        if (!StringUtils.equals(encode, resourceHeader)) {
            return ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.ACCESS_DENY));
        }
        String permission = currentTenant + "::" + resource.getResource();
        AuthRequest req = new AuthRequest();
        req.setPermission(permission);
        req.setToken(authHeader);
        return authClient.checkPermission(req, null)
                .flatMap((res) -> {
                    permissionCache.put(resourceHeader, res.isPermissionLegal());
                    if (Boolean.TRUE.equals(res.isPermissionLegal())) {
                        return chain.filter(exchange);
                    }
                    return ResponseUtil.writeResponse(exchange, Response.error(ResponseEnum.ACCESS_DENY));
                });
    }


}
