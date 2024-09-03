package com.visualization.model.api;

import com.visualization.utils.Base62Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
        spaceOptions = new ArrayList<>(tenantOptions.size() + 1);
        boolean flag = true;
        for (Option option : tenantOptions) {
            String s = option.getLabel() + "-" + Base62Util.zip(Long.parseLong(option.getValue()));
            spaceOptions.add(Option.builder()
                    .label(s)
                    .value(s)
                    .build());
            if (flag && option.getValue().equals(userId)) flag = false;
        }
        if (flag) {
            spaceOptions.add(Option.builder()
                    .label(oa)
                    .value(oa)
                    .build());
        }
    }
}
