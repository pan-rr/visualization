package com.visualization.client;


import com.github.benmanes.caffeine.cache.Cache;
import com.visualization.auth.model.AuthRequest;
import com.visualization.auth.utils.TenantPermissionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class AuthClient {

    @Resource(name = "permissionCache")
    private Cache<String, Boolean> cache;

    @Resource(name = "authRestTemplate")
    private RestTemplate template;

    @Value("${visual.auth.tenant-header:visual_tenant}")
    private String tenantHeader;

    @Value("${visual.auth.token.name:visual}")
    private String tokenName;


    public Boolean checkPermission(String permission) {
        ServletWebRequest request = (ServletWebRequest) RequestContextHolder.getRequestAttributes();
        String tenant = request.getHeader(tenantHeader);
        String token = request.getHeader(tokenName);
        String key = TenantPermissionUtil.computePermission(tenant, permission);
        Boolean value = cache.getIfPresent(key);
        if (Objects.nonNull(value)) return value;
        AuthRequest authRequest = new AuthRequest();
        authRequest.setPermission(permission);
        authRequest.setToken(token);
        value = template.postForObject("http://VISUAL-AUTH/auth/api/validatePermission", authRequest, Boolean.class);
        cache.put(key, value);
        return value;
    }
}
