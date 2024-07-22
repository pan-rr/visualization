package com.visualisation.model.dag.logicflow;

import com.google.gson.Gson;
import com.visualisation.constant.StatusConstant;
import com.visualisation.exception.DAGException;
import com.visualisation.jpa.SnowIdWorker;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicGraph {
    private LogicFlow logicFlow;
    private String name;

    public void validateDAG() {
        logicFlow.validateDAG();
        if (StringUtils.isEmpty(name)) {
            throw new DAGException("流程模版名称为空");
        }
    }

    public List<String> getNodeIds() {
        return logicFlow.getNodes().stream().map(LogicFlowNode::getId).collect(Collectors.toList());
    }

    public DAGTemplate getTemplate() {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        Gson gson = new Gson();
        String flowJSON = gson.toJson(logicFlow);
        return DAGTemplate.builder()
                .name(this.getName())
                .templateId(snowIdWorker.nextId())
                .graph(flowJSON)
                .status(StatusConstant.NORMAL)
                .build();
    }
    public void validateTaskCount(int draftCount , int realCount){
        if (draftCount != realCount){
            throw new DAGException("存在未配置的任务!");
        }
    }
    public void  overrideId(List<DraftTask> draftTasks , List<Task> tasks){
        validateTaskCount(draftTasks.size(),tasks.size());
        Map<String,String> idMap = new HashMap<>();
        int n = draftTasks.size();
        for (int i = 0; i < n; i++) {
            idMap.put(draftTasks.get(i).getId(), String.valueOf(tasks.get(i).getTaskId()));
        }
        logicFlow.overrideId(idMap);
    }
}
