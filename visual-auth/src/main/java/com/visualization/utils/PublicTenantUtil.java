package com.visualization.utils;

import com.visualization.model.api.Option;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicReference;

public class PublicTenantUtil {
    public static final AtomicReference<Option> PUBLIC_TENANT_OPTION = new AtomicReference<>(null);


    public static boolean permissionOfPublic(String permission){
        return StringUtils.isNotBlank(permission) && permission.startsWith(PUBLIC_TENANT_OPTION.get().getValue());
    }
}
