package com.visualization.auth.utils;

public class TenantPermissionUtil {

    public static String computePermission(String tenantId, String permission) {
        return tenantId + "::" + permission;
    }

}
