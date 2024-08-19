package com.visualization.manager.orphan;

import com.visualization.handler.JDBCHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;


public class SQLManager extends OrphanManager {

    public static void execute(Map<String, String> properties) {
        JDBCHandler jdbcHandler = new JDBCHandler(properties);
        NamedParameterJdbcTemplate template = jdbcHandler.getJdbcTemplate();
        String script = properties.get("script");
        template.getJdbcTemplate().execute(script);
    }
}
