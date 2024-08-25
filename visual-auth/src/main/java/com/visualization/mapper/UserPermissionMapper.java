package com.visualization.mapper;

import com.visualization.model.db.SystemUserPermission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserPermissionMapper {

    @Insert("insert into t_system_user_permission(user_id,permission_id) values(#{data.userId},#{data.permissionId})")
    void insertOne(@Param("data") SystemUserPermission systemUserPermission);

    @Select("select * from t_system_user_permission where user_id = #{data.userId} and permission_id = #{data.permissionId} limit 1")
    SystemUserPermission selectOne(@Param("data") SystemUserPermission systemUserPermission);
}
