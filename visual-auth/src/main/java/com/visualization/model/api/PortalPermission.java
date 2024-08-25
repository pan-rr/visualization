package com.visualization.model.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.visualization.model.db.SystemPermission;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortalPermission {

    @NonNull
    private String tenantId;

    private String permissionId;

    private String permissionName;

    private List<String> resourceList;

    public static PortalPermission covert(SystemPermission permission) {
        PortalPermission build = PortalPermission.builder()
                .permissionId(permission.getPermissionId().toString())
                .permissionName(permission.getPermissionName())
                .tenantId(permission.getTenantId().toString())
                .build();
        if (StringUtils.isNotBlank(permission.getResourceList())) {
            build.resourceList = Arrays.stream(permission.getResourceList().split(",")).collect(Collectors.toList());
        }
        return build;
    }

    public static List<PortalPermission> covert(List<SystemPermission> list) {
        return list.stream().map(PortalPermission::covert).collect(Collectors.toList());
    }

    public Wrapper<SystemPermission> buildWrapper() {
        QueryWrapper<SystemPermission> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<SystemPermission> lambda = queryWrapper.lambda();
        lambda.eq(SystemPermission::getTenantId, Long.valueOf(tenantId));
        if (StringUtils.isNoneBlank(permissionName)) lambda.eq(SystemPermission::getPermissionName, permissionName);
        return queryWrapper;
    }
}
