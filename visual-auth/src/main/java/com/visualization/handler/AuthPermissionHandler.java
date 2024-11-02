package com.visualization.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.exeception.AuthException;
import com.visualization.mapper.ResourceMapper;
import com.visualization.mapper.TenantMapper;
import com.visualization.model.db.SystemResource;
import com.visualization.utils.OwnerUtil;
import com.visualization.utils.PublicTenantUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthPermissionHandler {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private ResourceMapper resourceMapper;

    private static final String prefix = "visual:permission:";

    private String permissionKey(Long userId) {
        return prefix + userId;
    }

    public void loadPermission(Long userId) {
        List<String> permissions = new LinkedList<>();
        permissions.addAll(getRole(userId));
        permissions.addAll(getPermission(userId));
        String key = permissionKey(userId);
        redisTemplate.opsForSet().add(key, permissions.toArray(new String[0]));
        redisTemplate.expire(key, Duration.of(30, ChronoUnit.MINUTES));
    }

    public void cleanPermission() {
        redisTemplate.delete(permissionKey(StpUtil.getLoginIdAsLong()));
    }

    public List<String> getRole(Long userId) {
        RoleHandler handler = RoleHandler.builder()
                .id(userId)
                .tenantMapper(tenantMapper)
                .build();
        return handler.computeRole();
    }

    public List<String> getPermission(Long userId) {
        Set<Long> userResource = resourceMapper.selectUserResourceList(userId)
                .stream()
                .flatMap(resources -> Arrays.stream(resources.split(",")).map(Long::valueOf))
                .collect(Collectors.toSet());
        List<SystemResource> allResource = resourceMapper.selectTenantResourceByUserId(userId);
        return allResource.stream()
                .filter(i -> userResource.contains(i.getResourceId()))
                .map(SystemResource::computeResourceKey)
                .collect(Collectors.toList());
    }

    public List<String> getPermissionList(Long userId) {
        String key = permissionKey(userId);
        Set<String> members = redisTemplate.opsForSet().members(key);
        return CollectionUtils.isEmpty(members) ? getPermission(userId) : new ArrayList<>(members);
    }

    public boolean permissionValidate(String token, String permission) {
        boolean flag = PublicTenantUtil.permissionOfPublic(permission);
        if (flag) return true;
        Object id = StpUtil.getLoginIdByToken(token);
        if (Objects.isNull(id)) {
            throw new AuthException("该用户未登录");
        }
        Long userId = NumberUtils.createLong(id.toString());
        String key = permissionKey(userId);
        Long size = redisTemplate.opsForSet().size(key);
        if (Objects.isNull(size)) {
            loadPermission(userId);
        }
        String owner = OwnerUtil.getOwnerRoleByPermission(permission);
        Map<Object, Boolean> member = redisTemplate.opsForSet().isMember(key, permission, owner);
        return !CollectionUtils.isEmpty(member) && member.containsValue(true);
    }
}
