package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_resource")
public class SystemResource {

    private Long resourceId;

    private Long tenantId;

    private String resourceName;

    public String computeResourceKey() {
        return tenantId + ":" + resourceName + ":" + resourceId;
    }

}
