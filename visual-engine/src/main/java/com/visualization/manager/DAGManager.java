package com.visualization.manager;

import com.visualization.builder.VisualStageBuilder;
import com.visualization.enums.StatusEnum;
import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.service.DAGService;
import com.visualization.service.MinIOService;
import com.visualization.service.TaskService;
import com.visualization.view.base.VisualStage;
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


    @Resource
    private DAGService dagService;

    @Resource
    private TaskService taskService;

    @Resource(name = "dagTransactionTemplate")
    private TransactionTemplate transactionTemplate;

    @Resource
    private MinIOService minIOService;

    @Resource
    private FilePathMappingRepository filePathMappingRepository;

    @Resource
    private DAGDataSourceManager dagDataSourceManager;


    public void executeStage(DAGPointer pointer) {
        TaskKey taskKey = pointer.generateTaskKey();
        DAGTemplate template = dagService.getTemplateByPointer(pointer);
        dagService.makeSurePointerConfigMatchTemplate(pointer,template);
        List<Edge> nextEdges = dagService.findNextEdges(taskKey);
        VisualStage visualStage = VisualStageBuilder.builder()
                .dagPointer(pointer)
                .taskService(taskService)
                .dagDataSourceManager(dagDataSourceManager)
                .filePathMappingRepository(filePathMappingRepository)
                .build().buildStage();
        visualStage.execute();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                dagService.deletePointer(taskKey);
                dagService.tryFinishInstance(pointer.getInstanceId());
                if (!CollectionUtils.isEmpty(nextEdges)) {
                    List<DAGPointer> pointers = nextEdges
                            .stream()
                            .filter(edge -> !Objects.equals(edge.getToTaskId(), edge.getFromTaskId()))
                            .map(edge -> DAGPointer.builder()
                                    .instanceId(edge.getInstanceId())
                                    .taskId(edge.getToTaskId())
                                    .count(0)
                                    .templateId(template.getTemplateId())
                                    .status(StatusEnum.NORMAL.getStatus())
                                    .space(pointer.getSpace())
                                    .retryMaxCount(template.getRetryCount())
                                    .priority(template.getPriority())
                                    .build()).collect(Collectors.toList());
                    List<Long> readyIds = dagService.saveReadyPointers(pointers);
                    dagService.deleteEdges(EdgeId.computeId(pointer, readyIds));
                }
            }
        });
    }


    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        return dagService.createInstanceByTemplateId(templateId);
    }


    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    public void updateTaskInfo(DAGPointer pointer) {
        dagService.updatePointer(pointer);
        if (!StatusEnum.statusCanFinish(pointer.getStatus())) {
            dagService.updateInstanceStatus(pointer.getInstanceId(), pointer.getStatus());
        }
    }

    public List<DAGPointer> getPointers(int limit) {
        return dagService.getPointers(limit);
    }

    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    public void saveDAGPack(LogicFlowPack pack) {
        dagService.saveTemplateByPack(pack);
        taskService.saveTask(pack.getTasks());
        uploadTemplate(pack);
    }

    public void disableTemplateById(Long templateId) {
        dagService.updateTemplateStatus(templateId, StatusEnum.FORBIDDEN.getStatus());
    }

    public void changeTemplateStatus(Long templateId, int status) {
        StatusEnum.validateStatus(status);
        dagService.updateTemplateStatus(templateId, status);
    }

    private void uploadTemplate(LogicFlowPack pack) {
        minIOService.uploadTemplateStr(pack.getTemplate());
    }
}
