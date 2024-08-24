package com.visualization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.visualization.model.db.SystemUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface UserMapper extends BaseMapper<SystemUser> {

    @Select("select * from t_system_user where oa = #{oa} limit 1")
    SystemUser selectOneByOA(@Param("oa")String oa);
}
