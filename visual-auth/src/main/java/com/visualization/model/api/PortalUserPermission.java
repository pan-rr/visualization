package com.visualization.model.api;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalUserPermission {

    @NotBlank(message = "oa不能为空")
    private String oa;

    @NotBlank(message = "权限ID不能为空")
    private String permissionId;

}
