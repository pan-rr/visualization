package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.visualization.auth.utils.TenantPermissionUtil;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_resource")
public class SystemResource {

    @TableId
    private Long resourceId;

    private Long tenantId;

    private String resourceName;

    public String computeResourceKey() {
        return TenantPermissionUtil.computePermission(tenantId, resourceName);
    }

}
