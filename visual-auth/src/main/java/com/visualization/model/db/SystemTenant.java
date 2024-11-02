package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.visualization.utils.OwnerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_tenant")
public class SystemTenant {

    @TableId
    private Long tenantId;

    private String tenantName;

    private Long fatherId;

    private Long rootId;

    public String computeOwnerRole() {
        return OwnerUtil.computeOwnerRole(tenantId);
    }

    public boolean hasFather() {
        return Objects.nonNull(fatherId);
    }
}
