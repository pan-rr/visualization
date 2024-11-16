package com.visualization.manager.orphan;

import com.visualization.constant.ViewConstant;
import com.visualization.handler.JDBCHandler;
import com.visualization.view.base.VisualStage;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;


public class SQLManager {

    public static void execute(VisualStage stage) {
        Map<String, String> properties = stage.getSql();
        JDBCHandler jdbcHandler = new JDBCHandler(properties);
        NamedParameterJdbcTemplate template = jdbcHandler.getJdbcTemplate();
        String script = properties.get(ViewConstant.SCRIPT);
        template.getJdbcTemplate().execute(script);
    }

}
