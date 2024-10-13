package com.visualization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.visualization.model.db.SystemUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface UserMapper extends BaseMapper<SystemUser> {

    @Select("select * from t_system_user where oa = #{oa} limit 1")
    SystemUser selectOneByOA(@Param("oa") String oa);

    @Update("update t_system_user set password = #{newPassword} where user_id = #{userId} and password = #{oldPassword} limit 1")
    int updatePassword(@Param("userId") String userId, @Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword);
}
