package com.visualization.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.utils.PublicTenantUtil;
import com.visualization.mapper.TenantMapper;
import com.visualization.model.api.AuthResource;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemTenant;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public class AuthResourceHandler {

    private String tenantId;

    private TenantMapper tenantMapper;

    public AuthResource handle() {
        String userId = StpUtil.getLoginId().toString();
        Option publicOption = PublicTenantUtil.PUBLIC_TENANT_OPTION.get();
        if (publicOption.getValue().equals(tenantId) || StringUtils.equals(tenantId, userId) || checkTenantIsUserSubTenant()) {
            return AuthResource.builder().authKey(tenantId).permitAll(true).build();
        }
        List<String> resources = StpUtil.getPermissionList().stream().filter(s -> s.startsWith(tenantId)).collect(Collectors.toList());
        return AuthResource.builder().resourceList(resources).authKey(tenantId).permitAll(false).build();
    }

    private boolean checkTenantIsUserSubTenant() {
        long user = Long.parseLong(StpUtil.getLoginId().toString());
        long tenant = Long.parseLong(tenantId);
        if (user > tenant) return false;
        List<SystemTenant> list = tenantMapper.selectSameRootTenant(user);
        if (CollectionUtils.isEmpty(list)) return false;
        Map<Long, List<SystemTenant>> map = list.stream().collect(Collectors.groupingBy(SystemTenant::getFatherId));
        LinkedList<SystemTenant> q = new LinkedList<>(map.get(user));
        int cnt;
        long next;
        SystemTenant t;
        while ((cnt = q.size()) > 0) {
            while (cnt-- > 0) {
                t = q.poll();
                next = t.getTenantId();
                if (next == tenant) return true;
                if (map.containsKey(next)) {
                    q.addAll(map.get(next));
                }
            }
        }
        return false;
    }
}
