package com.visualization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemResource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ResourceMapper extends BaseMapper<SystemResource> {

    @Select(value = "select a.resource_list from t_system_permission a inner join t_system_user_permission b on (a.permission_id = b.permission_id and b.user_id = #{userId})")
    List<String> selectUserResourceList(@Param("userId") Long userId);

    @Select(value = "select * from t_system_resource where tenant_id in (select b.tenant_id from t_system_user_permission a inner join t_system_permission b on (a.user_id = #{userId} and a.permission_id = b.permission_id))")
    List<SystemResource> selectTenantResourceByUserId(@Param("userId") Long userId);

    @Select(value = "select resource_id value , resource_name label from t_system_resource where tenant_id = #{tenantId}")
    List<Option> getResourceOptions(@Param("tenantId") Long tenantId);

    int saveResources(@Param("list") List<SystemResource> resources , @Param("tenantId") String tenantId);
}
