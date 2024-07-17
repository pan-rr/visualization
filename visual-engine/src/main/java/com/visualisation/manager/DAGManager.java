package com.visualisation.manager;

import com.visualisation.Commander;
import com.visualisation.exception.DAGException;
import com.visualisation.constant.StatusConstant;
import com.visualisation.model.dag.*;
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

    @Value("${visual.dag.edge.retryMaxCount}")
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
                    .filter(edge -> !Objects.equals(edge.getToTaskId(), edge.getFromTaskId()))
                    .map(edge -> DAGPointer.builder()
                            .instanceId(edge.getInstanceId())
                            .taskId(edge.getToTaskId())
                            .count(0)
                            .status(StatusConstant.NORMAL)
                            .retryMaxCount(retryMaxCount)
                            .build()).collect(Collectors.toList());
            dagService.saveReadyPointers(pointers);
        } else {
            dagService.tryFinishInstance(taskKey.getInstanceId());
        }
    }

    @Transactional(transactionManager = "transactionManagerDAG")
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        return dagService.createInstanceByTemplateId(templateId);
    }

    public void saveTemplate(DAGTemplate dagTemplate) {
        dagService.saveTemplate(dagTemplate);
    }

    public void saveTask(Task task) {
        taskService.saveTask(task);
    }

    @Transactional(transactionManager = "transactionManagerDAG")
    public void updateTaskInfo(DAGPointer pointer) {
        dagService.updatePointer(pointer);
        if (pointer.getRetryMaxCount() - pointer.getCount() < 1) {
            dagService.updateInstanceStatus(pointer.getInstanceId(), StatusConstant.BLOCK);
        }
    }

    public List<DAGPointer> getPointers(int limit) {
        return dagService.getPointers(limit);
    }
}
