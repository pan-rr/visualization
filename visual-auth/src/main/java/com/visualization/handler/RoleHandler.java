package com.visualization.handler;

import com.visualization.mapper.TenantMapper;
import com.visualization.model.db.SystemTenant;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Builder
public class RoleHandler {

    private Long id;

    private SystemTenant root;

    private List<SystemTenant> children;

    private TenantMapper tenantMapper;

    public List<String> computeRole() {
        root = tenantMapper.selectById(id);
        if (Objects.isNull(root)) {
            return new ArrayList<>();
        }
        children = tenantMapper.selectSameRootTenant(id);
        Map<Long, List<SystemTenant>> map = children.stream().collect(Collectors.groupingBy(SystemTenant::getFatherId));
        LinkedList<SystemTenant> q = new LinkedList<>();
        q.offer(root);
        List<String> res = new LinkedList<>();
        int cnt;
        SystemTenant t;
        List<SystemTenant> arr;
        while ((cnt = q.size()) > 0) {
            while (cnt-- > 0) {
                t = q.poll();
                res.add(Objects.requireNonNull(t).computeOwnerRole());
                arr = map.get(t.getTenantId());
                if (!CollectionUtils.isEmpty(arr)) {
                    q.addAll(arr);
                }
            }
        }
        return res;
    }

}
