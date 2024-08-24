package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_permission")
public class SystemPermission {

    private Long permissionId;

    private Long tenantId;

    private String permissionName;

    private String resourceList;
}
