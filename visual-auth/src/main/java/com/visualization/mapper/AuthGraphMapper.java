package com.visualization.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuthGraphMapper {

    List<Map<String, Object>> getNodes(@Param("pre") String pre, @Param("level") int level);
}
