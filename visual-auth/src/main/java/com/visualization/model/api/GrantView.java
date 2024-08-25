package com.visualization.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantView {

    private String username;

    private String oa;

    private String permissionName;

    private String permissionId;

    private String tenantId;

}
