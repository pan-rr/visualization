package com.visualization.model.api;

import com.visualization.model.db.SystemResource;
import com.visualization.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortalResources {

    @NotBlank(message = "租户ID不能为空")
    private String tenantId;

    @NotNull(message = "资源不能为空")
    private List<String> resourceNames;

    public List<SystemResource> convert() {
        Long id = Long.valueOf(tenantId);
        return resourceNames.stream()
                .filter(StringUtils::isNoneBlank)
                .map(s -> SystemResource.builder().resourceId(SnowIdUtil.generateId()).tenantId(id).resourceName(s).build())
                .collect(Collectors.toList());
    }
}
