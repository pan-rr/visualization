package com.visualisation.view.base;

import com.visualisation.handler.JDBCHandler;
import com.visualisation.view.base.BaseView;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class JDBCBaseView extends BaseView {

    protected JDBCHandler JDBCHandler;

    protected void initHandler(Map<String, Object> param) {
        this.param = param;
        Map<String, String> jdbcProperties = new HashMap<>();
        param.forEach((k, v) -> jdbcProperties.put(k, String.valueOf(v)));
        JDBCHandler = new JDBCHandler(jdbcProperties);
    }

    protected NamedParameterJdbcTemplate getTargetJDBCTemplate() {
        return JDBCHandler.getJdbcTemplate();
    }

    protected String getDialectId() {
        return JDBCHandler.getDialectId();
    }

}
