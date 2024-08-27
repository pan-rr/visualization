package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_permission")
public class SystemPermission {

    private Long permissionId;

    @NotNull(message = "父租户ID不能为空")
    private Long tenantId;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @NotBlank(message = "资源不能为空")
    private String resourceList;
}
