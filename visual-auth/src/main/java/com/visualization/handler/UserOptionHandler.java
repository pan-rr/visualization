package com.visualization.handler;

import com.visualization.constant.TenantConstant;
import com.visualization.enums.UserTypeEnum;
import com.visualization.mapper.TenantMapper;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Builder
public class UserOptionHandler {

    private TenantMapper tenantMapper;

    private SystemUser user;

    public List<Option> computeOption() {
        Set<Option> res = new HashSet<>(tenantMapper.selectUserTenant(user.getUserId()));
        res.add(TenantConstant.PUBLIC_TENANT_OPTION.get());
        if (UserTypeEnum.isTenant(user.getUserType())) {
            List<SystemTenant> list = tenantMapper.selectTenantByTenantId(user.getUserId());
            Map<Long, List<SystemTenant>> map = list.stream().collect(Collectors.groupingBy(SystemTenant::getFatherId));
            LinkedList<Long> q = new LinkedList<>();
            res.add(Option.builder().label(user.getUsername()).value(String.valueOf(user.getUserId())).build());
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
                            res.add(Option.builder().label(tenant.getTenantName()).value(tenant.getTenantId().toString()).build());
                        }
                    }
                }
            }
        }
        return new ArrayList<>(res);
    }
}
