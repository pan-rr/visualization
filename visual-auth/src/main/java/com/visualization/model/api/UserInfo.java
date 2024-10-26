package com.visualization.model.api;

import com.visualization.constant.AuthConstant;
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
                    .value(option.getValue())
                    .build());
            if (flag && option.getValue().equals(userId)) flag = false;
        }
        if (flag) {
            String s = name + "-" + Base62Util.zip(Long.parseLong(userId));
            spaceOptions.add(Option.builder()
                    .label(s)
                    .value(userId)
                    .build());
        }
        sortOption();
    }

    private void sortOption() {
        tenantOptions.sort((o1, o2) -> {
            String label = o1.getLabel();
            if (oa.equals(label) || AuthConstant.PUBLIC.equals(label)) {
                return -1;
            }
            return o1.getValue().compareTo(o2.getValue());
        });
        spaceOptions.sort((o1, o2) -> {
            String label = o1.getLabel();
            if (oa.equals(label) || AuthConstant.PUBLIC.equals(label)) {
                return -1;
            }
            return o1.getValue().compareTo(o2.getValue());
        });
    }
}
