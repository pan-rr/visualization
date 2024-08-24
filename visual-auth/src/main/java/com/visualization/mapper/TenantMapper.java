package com.visualization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemTenant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TenantMapper extends BaseMapper<SystemTenant> {

    @Select(value = "select c.tenant_name label , c.tenant_id value from t_system_permission a inner join t_system_user_permission b on b.user_id = #{userId} inner join t_system_tenant c on a.tenant_id = c.tenant_id")
    List<Option> selectUserTenant(@Param("userId")Long userId);
}
