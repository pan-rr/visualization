package com.visualization.utils;

import org.apache.commons.lang3.StringUtils;

public class OwnerUtil {

    private static final String OWNER = "::owner";

    public static String computeOwnerRole(Long tenantId) {
        return tenantId + OWNER;
    }

    public static String getOwnerRoleByPermission(String permission) {
        return StringUtils.isNotBlank(permission) ? permission.split("::")[0] + OWNER : "";
    }
}
