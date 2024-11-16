package com.visualization.model.dag.logicflow;

import com.google.gson.Gson;
import com.visualization.enums.Status;
import com.visualization.exception.DAGException;
import com.visualization.jpa.SnowIdWorker;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.dag.db.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicGraph {

    private LogicFlow logicFlow;

    private String name;

    private String space;

    private Integer retryCount;

    private Double priority;

    private String context;

    public LogicFlowPack getPack() {
        this.validateDAG();
        List<Task> tasks = this.getTasks();
        DAGTemplate template = this.getTemplate();
        return LogicFlowPack.builder().template(template).tasks(tasks).build();
    }

    public void validateDAG() {
        if (StringUtils.isEmpty(name)) {
            throw new DAGException("流程模版名称不能为空！");
        }
        logicFlow.validateDAG();
    }

    public DAGTemplate getTemplate() {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        Gson gson = new Gson();
        String flowJSON = gson.toJson(logicFlow);
        return DAGTemplate.builder()
                .name(this.getName())
                .templateId(snowIdWorker.nextId())
                .json(flowJSON)
                .space(space)
                .retryCount(this.retryCount)
                .priority(this.priority)
                .context(StringUtils.isNotBlank(context) ? context : "{}")
                .totalTaskCount(logicFlow.getNodes().size())
                .status(Status.NORMAL.getStatus())
                .build();
    }

    public List<Task> getTasks() {
        return logicFlow.getTasks();
    }

    public void handleRetryCount(Integer defaultCount) {
        if (Objects.isNull(this.retryCount)) {
            this.retryCount = defaultCount;
        }
    }

    public void handlePriority(Double defaultPriority) {
        if (Objects.isNull(this.priority)) {
            this.priority = defaultPriority;
        }
    }
}
