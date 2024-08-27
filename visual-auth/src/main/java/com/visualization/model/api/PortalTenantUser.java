package com.visualization.model.api;

import com.visualization.enums.UserTypeEnum;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.utils.MD5Utils;
import com.visualization.utils.SnowIdUtil;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalTenantUser {

    private String fatherId;

    @NonNull
    private String oa;

    @NonNull
    private String username;

    @NonNull
    private String password;

    private Integer userType;

    public SystemUser buildUser() {
        LocalDateTime now = LocalDateTime.now();
        return SystemUser.builder()
                .userId(SnowIdUtil.generateId())
                .userType(UserTypeEnum.TENANT.getCode())
                .oa(oa)
                .username(username)
                .password(MD5Utils.md5Encode(password))
                .modifyTime(now)
                .createTime(now)
                .build();
    }

    public SystemTenant buildTenant() {
        return SystemTenant.builder()
                .tenantId(SnowIdUtil.generateId())
                .fatherId(Long.valueOf(fatherId))
                .tenantName(username)
                .build();
    }
}
