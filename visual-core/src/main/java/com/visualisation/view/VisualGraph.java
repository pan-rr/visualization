package com.visualisation.view;

import com.visualisation.handler.rewrite.ViewRewriteHandler;
import com.visualisation.handler.SpringApplicationHandler;
import com.visualisation.manager.ViewManager;
import com.visualisation.manager.orphan.SQLManager;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VisualGraph {

    private List<Map<String, Object>> input;
    private Map<String, Object> output;

    private Map<String, String> sql;


    public List<Map<String, Object>> getInput() {
        return input;
    }

    public void setInput(List<Map<String, Object>> input) {
        this.input = input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public List<ViewConf> getInputView() {
        return input.stream().map(ViewConf::new).collect(Collectors.toList());
    }

    public VisualGraph() {
    }

    public void filter(Collection<ViewRewriteHandler> filters) {
        for (ViewRewriteHandler filter : filters) {
            filter.handle(output);
            input.forEach(filter::handle);
        }
    }

    public ViewConf getOutputView() {
        return new ViewConf(output);
    }

    public Map<String, String> getSql() {
        return sql;
    }

    public void setSql(Map<String, String> sql) {
        this.sql = sql;
    }

    public void execute() {
        if (!CollectionUtils.isEmpty(input) && !CollectionUtils.isEmpty(output)) {
            executeNormal();
        } else if (!CollectionUtils.isEmpty(sql)) {
            executeSQL();
        } else {
            throw new RuntimeException("无法执行非法的配置！");
        }

    }

    private void executeNormal() {
        ApplicationContext ctx = SpringApplicationHandler.getCtx();
        DataSource db = ctx.getBean("db", DataSource.class);
        Collection<ViewRewriteHandler> viewRewriteHandlers = ctx.getBeansOfType(ViewRewriteHandler.class).values();
        this.filter(viewRewriteHandlers);
        ViewManager viewManager = new ViewManager(this.getInputView(), this.getOutputView(), db);
        viewManager.execute();
    }

    private void executeSQL() {
        SQLManager.execute(sql);
    }
}
