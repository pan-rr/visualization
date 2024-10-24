package com.visualization.view.base;

import com.visualization.constant.TaskTypeConstant;
import com.visualization.handler.SpringContextHandler;
import com.visualization.handler.rewrite.ViewRewriteHandler;
import com.visualization.manager.ViewManager;
import com.visualization.manager.orphan.SQLManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VisualStage {

    private List<Map<String, Object>> input;
    private Map<String, Object> output;
    private Map<String, String> sql;
    private String taskType;


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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public VisualStage() {
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
        if (StringUtils.isBlank(taskType)) {
            throw new RuntimeException("任务类型为空！");
        }
        switch (taskType) {
            case TaskTypeConstant.VISUAL:
                executeNormal();
                break;
            case TaskTypeConstant.SQL:
                executeSQL();
                break;
            default:
                throw new RuntimeException("无法执行非法的配置！");
        }
    }

    private void executeNormal() {
        ApplicationContext ctx = SpringContextHandler.getCtx();
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
