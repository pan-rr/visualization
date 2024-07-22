package com.visualisation.manager;

import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.logicflow.DraftTask;
import com.visualisation.model.dag.logicflow.LogicFlowPack;
import com.visualisation.model.dag.logicflow.LogicGraph;
import com.visualisation.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class LogicFlowManager {

    @Resource
    private TaskService taskService;

    @Resource
    private DAGManager dagManager;


    public void saveDraftTask(DraftTask draftTask){
        taskService.saveDraftTask(draftTask);
    }


    public void saveTemplate(LogicGraph graph){
        graph.validateDAG();
        List<String> nodeIds = graph.getNodeIds();
        List<DraftTask> draftTasks = taskService.getDraftTaskListByIDList(nodeIds);
        List<Task> tasks = DraftTask.convert(draftTasks);
        graph.overrideId(draftTasks,tasks);
        DAGTemplate template = graph.getTemplate();
        LogicFlowPack pack = LogicFlowPack.builder().template(template).tasks(tasks).draftTaskIds(nodeIds).build();
        dagManager.saveDAGPack(pack);
    }
}
