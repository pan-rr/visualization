package com.visualization.manager;

import com.visualization.enums.Status;
import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.repository.file.FilePathMappingRepository;
import com.visualization.service.DAGService;
import com.visualization.service.MinIOService;
import com.visualization.service.TaskService;
import com.visualization.stage.VisualStageBuilder;
import com.visualization.stage.VisualStageContextService;
import com.visualization.runtime.VisualStage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private VisualStageContextService visualStageContextService;

    private List<DAGPointer> computeNextPointers(DAGPointer pointer) {
        List<Edge> edges = dagService.findNextEdges(pointer);
        if (!CollectionUtils.isEmpty(edges)) {
            return edges.stream()
                    .filter(edge -> !Objects.equals(edge.getToTaskId(), edge.getFromTaskId()))
                    .map(edge -> DAGPointer.builder()
                            .instanceId(edge.getInstanceId())
                            .taskId(edge.getToTaskId())
                            .count(0)
                            .templateId(pointer.getTemplateId())
                            .status(Status.NORMAL.getStatus())
                            .space(pointer.getSpace())
                            .retryMaxCount(pointer.getRetryMaxCount())
                            .priority(pointer.getPriority())
                            .build()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void executeStage(DAGPointer pointer) {

        InstanceContext context = visualStageContextService.getById(pointer.getInstanceId());
        List<DAGPointer> nextPointers = computeNextPointers(pointer);

        VisualStage visualStage = VisualStageBuilder.builder()
                .dagPointer(pointer)
                .taskService(taskService)
                .dagDataSourceManager(dagDataSourceManager)
                .filePathMappingRepository(filePathMappingRepository)
                .context(context.getCtx())
                .build().buildStage();
        visualStage.execute();

        // 中止实例不执行后续操作
        Long instanceId = pointer.getInstanceId();
        DAGInstance instance = dagService.getInstance(instanceId);
        if (Objects.isNull(instance) || Status.USER_TERMINATE.getStatus().equals(instance.getStatus())) {
            return;
        }
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NotNull TransactionStatus status) {
                visualStageContextService.updateContext(context);
                dagService.deletePointer(pointer);
                dagService.tryFinishInstance(instanceId);
                if (!CollectionUtils.isEmpty(nextPointers)) {
                    List<Long> readyIds = dagService.saveReadyPointers(nextPointers,instanceId);
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
    public void terminateInstance(Long instanceId) {
        dagService.updateInstanceStatus(instanceId,Status.USER_TERMINATE.getStatus());
    }


    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    public void handleFailStage(DAGPointer pointer) {
        dagService.updatePointer(pointer);
        if (!Status.statusCanFinish(pointer.getStatus())) {
            dagService.updateInstanceStatus(pointer.getInstanceId(), pointer.getStatus());
        }
    }

    public List<DAGPointer> getExecutablePointers(int limit) {
        return dagService.getExecutablePointers(limit);
    }

    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    public void saveDAGPack(LogicFlowPack pack) {
        dagService.saveTemplateByPack(pack);
        taskService.saveTask(pack.getTasks());
        uploadTemplate(pack);
    }

    public void disableTemplateById(Long templateId) {
        dagService.updateTemplateStatus(templateId, Status.FORBIDDEN.getStatus());
    }

    public void changeTemplateStatus(Long templateId, int status) {
        Status.validateStatus(status);
        dagService.updateTemplateStatus(templateId, status);
    }

    private void uploadTemplate(LogicFlowPack pack) {
        minIOService.uploadTemplateStr(pack.getTemplate());
    }
}
