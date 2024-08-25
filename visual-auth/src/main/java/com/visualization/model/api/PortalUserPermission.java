package com.visualization.model.api;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalUserPermission {

    @NonNull
    private String oa;

    @NonNull
    private String permissionId;

}
