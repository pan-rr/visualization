package com.visualisation.manager;

import com.visualisation.Commander;
import com.visualisation.DAGException;
import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.Edge;
import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.TaskKey;
import com.visualisation.service.DAGService;
import com.visualisation.service.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DAGManager {

    @Value("${dag.edge.retryMaxCount}")
    private Integer retryMaxCount;

    @Resource
    private DAGService dagService;

    @Resource
    private TaskService taskService;

    @Resource
    private Commander commander;


    @Transactional(transactionManager = "transactionManagerDAG")
    public void executeTask(DAGPointer pointer) {
        TaskKey taskKey = pointer.getTaskKey();
        Task task = taskService.getTaskById(taskKey.getTaskId());
        if (Objects.isNull(task)) {
            throw new DAGException("任务为空，id:" + taskKey);
        }
        commander.commandWithJsonString(task.getJson());
        dagService.deletePointer(taskKey);
        List<Edge> nextEdges = dagService.findNextEdges(taskKey);
        if (!CollectionUtils.isEmpty(nextEdges)) {
            List<DAGPointer> pointers = nextEdges
                    .stream()
                    .filter(edge -> Objects.nonNull(edge.getToTaskId()))
                    .map(edge -> DAGPointer.builder()
                            .instanceId(edge.getInstanceId())
                            .taskId(edge.getToTaskId())
                            .count(0)
                            .retryMaxCount(retryMaxCount)
                            .build()).collect(Collectors.toList());
            dagService.saveReadyPointers(pointers);
        }
    }
}
