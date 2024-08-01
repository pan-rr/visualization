package com.visualisation.manager;

import com.visualisation.constant.StatusConstant;
import com.visualisation.model.dag.*;
import com.visualisation.model.dag.logicflow.LogicFlowPack;
import com.visualisation.service.DAGService;
import com.visualisation.service.MinIOService;
import com.visualisation.service.TaskService;
import com.visualisation.view.VisualGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
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

    @Resource(name = "dagTransactionTemplate")
    private TransactionTemplate transactionTemplate;

    @Resource
    private MinIOService minIOService;


    public void executeTask(DAGPointer pointer) {
        TaskKey taskKey = pointer.generateTaskKey();
        List<Edge> nextEdges = dagService.findNextEdges(taskKey);
        VisualGraph visualGraph = pointer.buildGraph(taskService);
        visualGraph.execute();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                dagService.deletePointer(taskKey);
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
        });
    }


    @Transactional(transactionManager = "transactionManagerDAG")
    public DAGInstance createLogicFlowInstanceByTemplateId(Long templateId) {
        return dagService.createLogicFlowInstanceByTemplateId(templateId);
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

    @Transactional(transactionManager = "transactionManagerDAG")
    public void saveDAGPack(LogicFlowPack pack) {
        dagService.saveTemplateByPack(pack);
        taskService.saveTask(pack.getTasks());
        uploadTemplate(pack);
    }

    public void disableTemplateById(Long templateId) {
        dagService.updateTemplateStatus(templateId, StatusConstant.FORBIDDEN);
    }

    private void uploadTemplate(LogicFlowPack pack) {
        minIOService.uploadTemplateStr(pack.getTemplate());
    }
}
