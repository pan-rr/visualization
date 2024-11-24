package com.visualization.model.api;

import com.visualization.enums.UserStatusEnum;
import com.visualization.enums.UserTypeEnum;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.utils.MD5Utils;
import com.visualization.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalTenantUser {

    private String fatherId;

    @NotBlank(message = "oa不能为空")
    private String oa;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private Integer userType;

    public Tuple2<SystemUser, SystemTenant> registerSubTenant() {
        LocalDateTime now = LocalDateTime.now();
        long id = SnowIdUtil.generateId();
        SystemUser user = SystemUser.builder()
                .userId(id).userType(UserTypeEnum.TENANT.getCode()).oa(oa)
                .username(username).status(UserStatusEnum.NORMAL.getCode())
                .password(MD5Utils.encode(password)).modifyTime(now)
                .createTime(now).build();
        SystemTenant tenant = SystemTenant.builder()
                .tenantId(id)
                .fatherId(Long.valueOf(fatherId))
                .tenantName(username)
                .build();
        return Tuples.of(user, tenant);
    }
}
