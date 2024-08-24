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
@TableName(value = "t_system_tenant")
public class SystemTenant {

    private Long tenantId;

    private String tenantName;

    private Long fatherId;

    private Long rootId;

}
