package com.visualization.handler;

import com.visualization.enums.UserTypeEnum;
import com.visualization.mapper.TenantMapper;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.utils.PublicTenantUtil;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public class TenantOptionHandler {

    private TenantMapper tenantMapper;

    private SystemUser user;

    public List<Option> computeTenantOption() {
        Set<Option> set = tenantMapper.selectUserTenant(user.getUserId());
        List<Option> res = new LinkedList<>();
        if (UserTypeEnum.isTenant(user.getUserType())) {
            res.add(Option.builder().label(user.getUsername()).value(String.valueOf(user.getUserId())).build());
            List<SystemTenant> list = tenantMapper.selectSameRootTenant(user.getUserId());
            Map<Long, List<SystemTenant>> map = list.stream().filter(SystemTenant::hasFather).collect(Collectors.groupingBy(SystemTenant::getFatherId));
            LinkedList<Long> q = new LinkedList<>();
            set.add(Option.builder().label(user.getUsername()).value(String.valueOf(user.getUserId())).build());
            q.add(user.getUserId());
            int cnt;
            Long t;
            List<SystemTenant> arr;
            while ((cnt = q.size()) > 0) {
                while (cnt-- > 0) {
                    t = q.poll();
                    arr = map.get(t);
                    if (!CollectionUtils.isEmpty(arr)) {
                        for (SystemTenant tenant : arr) {
                            q.add(tenant.getTenantId());
                            set.add(Option.builder().label(tenant.getTenantName()).value(tenant.getTenantId().toString()).build());
                        }
                    }
                }
            }
        }
        res.addAll(set);
        res.add(PublicTenantUtil.PUBLIC_TENANT_OPTION.get());
        return res.stream().distinct().collect(Collectors.toList());
    }
}
