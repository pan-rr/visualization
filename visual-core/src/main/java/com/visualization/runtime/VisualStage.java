package com.visualization.runtime;

import com.visualization.constant.TaskTypeConstant;
import com.visualization.handler.rewrite.ViewRewriteHandler;
import com.visualization.manager.ViewManager;
import com.visualization.manager.orphan.FlinkManager;
import com.visualization.manager.orphan.HttpManager;
import com.visualization.manager.orphan.SQLManager;
import com.visualization.view.base.ViewConf;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class VisualStage {

    private List<Map<String, Object>> input;
    private Map<String, Object> output;
    private Map<String, String> sql;
    private Map<String, Object> contextInject;
    private Map<String, Object> visualContext;
    private Map<String, Object> http;
    private Map<String, Object> flink;
    private String taskType;

    private VContext runtimeContext;


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

    public Map<String, Object> getVisualContext() {
        return visualContext;
    }

    public void setVisualContext(Map<String, Object> visualContext) {
        this.visualContext = visualContext;
    }

    public Map<String, Object> getContextInject() {
        return contextInject;
    }

    public void setContextInject(Map<String, Object> contextInject) {
        this.contextInject = contextInject;
    }

    public Map<String, Object> getHttp() {
        return http;
    }

    public void setHttp(Map<String, Object> http) {
        this.http = http;
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

    public Map<String, Object> getFlink() {
        return flink;
    }

    public void setFlink(Map<String, Object> flink) {
        this.flink = flink;
    }

    private void initRuntimeContext() {
        if (Objects.nonNull(this.runtimeContext)) return;
        this.runtimeContext = new VStageContext(visualContext);
    }

    public void setRuntimeContext(VContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public VContext getRuntimeContext() {
        return runtimeContext;
    }

    public VContext execute() {
        if (StringUtils.isBlank(taskType)) {
            throw new RuntimeException("任务类型为空！");
        }
        this.initRuntimeContext();
        switch (taskType) {
            case TaskTypeConstant.VISUAL:
                ViewManager viewManager = new ViewManager(this);
                viewManager.execute();
                break;
            case TaskTypeConstant.SQL:
                SQLManager.execute(this);
                break;
            case TaskTypeConstant.HTTP:
                HttpManager.execute(this);
                break;
            case TaskTypeConstant.FLINK:
                FlinkManager.execute(this);
                break;
            default:
                throw new RuntimeException("无法执行非法的配置！");
        }
        return this.runtimeContext;
    }

}
