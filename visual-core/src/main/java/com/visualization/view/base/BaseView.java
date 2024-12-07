package com.visualization.view.base;

import com.visualization.constant.ViewConstant;
import com.visualization.runtime.VContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Map;

public abstract class BaseView implements View {

    protected Map<String, Object> param;

    protected Map<String, Object> properties;

    protected Map<String, Object> tableNames;

    protected String realTableName;

    protected String rawTableName;

    protected int id;

    protected String script;

    protected DataSource federationDataSource;

    protected JdbcTemplate jdbcTemplate;

    protected VContext context;

    protected int getId() {
        return id;
    }

    public String getRealTableName() {
        realTableName = new StringBuilder("t_")
                .append(getId())
                .append(getRawTableName())
                .toString();
        return realTableName;
    }

    public String getRawTableName() {
        rawTableName = String.valueOf(properties.get(ViewConstant.TABLE_NAME));
        return rawTableName;
    }

    public String getScript() {
        script = String.valueOf(properties.get(ViewConstant.SCRIPT));
        return script;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public void setTableNames(Map<String, Object> names) {
        tableNames = names;
        String name = getRawTableName();
        if (name != null) tableNames.put(name, getRealTableName());
    }

    @Override
    public void destroy() {
        String sql = MessageFormat.format(destroySQL, this.getRealTableName());
        jdbcTemplate.execute(sql);
    }

    public void setContext(VContext context) {
        this.context = context;
    }
}
