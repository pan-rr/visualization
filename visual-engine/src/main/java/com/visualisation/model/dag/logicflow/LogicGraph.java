package com.visualisation.model.dag.logicflow;

import com.google.gson.Gson;
import com.visualisation.constant.StatusConstant;
import com.visualisation.constant.VisualConstant;
import com.visualisation.exception.DAGException;
import com.visualisation.jpa.SnowIdWorker;
import com.visualisation.model.dag.db.DAGTemplate;
import com.visualisation.model.dag.db.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

    public LogicFlowPack getPack(){
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
                .space(Objects.nonNull(space) ? space : VisualConstant.DEFAULT_SPACE)
                .status(StatusConstant.NORMAL)
                .build();
    }

    public List<Task> getTasks(){
        return logicFlow.getTasks();
    }
}
