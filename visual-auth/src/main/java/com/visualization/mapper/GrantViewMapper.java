package com.visualization.mapper;

import com.visualization.model.api.GrantView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GrantViewMapper {

    List<GrantView> selectGrantViewList(@Param("data")GrantView grantView);

    void retractPermission(@Param("userId")Long userId,@Param("permissionId")Long permissionId);
}
