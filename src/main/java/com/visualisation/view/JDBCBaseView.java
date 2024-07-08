package com.visualisation.view;

import com.visualisation.handler.JDBCHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

public abstract class JDBCBaseView extends BaseView{

    protected JDBCHandler JDBCHandler;

    protected void initHandler(Map<String, String> param) {
        this.param = param;
        JDBCHandler = new JDBCHandler(param);
    }

    protected NamedParameterJdbcTemplate getTargetJDBCTemplate() {
        return JDBCHandler.getJdbcTemplate();
    }

    protected String getDialectId(){
        return JDBCHandler.getDialectId();
    }

}
