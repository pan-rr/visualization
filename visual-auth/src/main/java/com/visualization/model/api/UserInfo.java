package com.visualization.model.api;

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
        tenantOptions.add(Option.builder().value("public").label("public").build());
        spaceOptions = new ArrayList<>(tenantOptions.size() + 1);
        spaceOptions.add(Option.builder()
                .label(oa)
                .value(oa)
                .build());
        for (Option option : tenantOptions) {
            String str = option.getLabel() + "::" + option.getValue();
            spaceOptions.add(Option.builder()
                    .label(option.getLabel())
                    .value(str)
                    .build());
        }
    }
}
