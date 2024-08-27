package com.visualization.model.api;

import com.visualization.constant.TenantConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private String userId;

    private String name;

    private String oa;

    private String token;

    private List<Option> tenantOptions;

    private List<Option> spaceOptions;

    public void computeOptions() {
        tenantOptions.add(TenantConstant.PUBLIC_TENANT_OPTION.get());
        tenantOptions = tenantOptions.stream().distinct().collect(Collectors.toList());
        spaceOptions = new ArrayList<>(tenantOptions.size() + 1);
        boolean flag = true;
        for (Option option : tenantOptions) {
            spaceOptions.add(Option.builder()
                    .label(option.getLabel())
                    .value(option.getLabel())
                    .build());
            if (flag && option.getValue().equals(userId)) flag = false;
        }
        if (flag) {
            spaceOptions.add(Option.builder()
                    .label(oa)
                    .value(oa)
                    .build());
        }
        String publicValue = TenantConstant.PUBLIC_TENANT_OPTION.get().getValue();
        tenantOptions.sort(((o1, o2) -> {
            if (o2.getValue().equals(publicValue)) return -1;
            return o1.getValue().compareTo(o2.getValue());
        }));
    }
}
