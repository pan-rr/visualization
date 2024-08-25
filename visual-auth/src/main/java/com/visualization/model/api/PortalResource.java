package com.visualization.model.api;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.visualization.model.db.SystemResource;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_resource")
public class PortalResource {

    private String resourceId;

    @NonNull
    private String tenantId;

    @NonNull
    private String resourceName;

    public SystemResource convert(){
        return SystemResource.builder()
                .tenantId(Long.valueOf(tenantId))
                .resourceName(resourceName)
                .build();
    }

    public static List<PortalResource> convert(List<SystemResource> list){
        return list.stream().map(i -> PortalResource.builder()
                 .resourceName(i.getResourceName())
                 .tenantId(i.getTenantId().toString())
                 .resourceId(i.getResourceId().toString())
                 .build()).collect(Collectors.toList());
    }

    public Wrapper<SystemResource> buildWrapper(){
        SystemResource convert = this.convert();
        QueryWrapper<SystemResource> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<SystemResource> lambda = queryWrapper.lambda();
        lambda.eq(SystemResource::getTenantId, convert.getTenantId());
        if(StringUtils.isNoneBlank(resourceName))lambda.eq(SystemResource::getResourceName, convert.getResourceName());
        return queryWrapper;
    }
}
