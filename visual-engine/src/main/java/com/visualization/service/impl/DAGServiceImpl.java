package com.visualization.service.impl;

import com.visualization.constant.StatusConstant;
import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.repository.dag.*;
import com.visualization.service.DAGService;
import com.visualization.service.MinIOService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DAGServiceImpl implements DAGService {
    @Value("${visual.dag.edge.retryMaxCount}")
    private Integer retryMaxCount = 5;

    @Resource
    private EdgeRepository edgeRepository;

    @Resource
    private DAGTemplateRepository dagTemplateRepository;

    @Resource
    private DAGPointerRepository dagPointerRepository;

    @Resource
    private TaskLatchRepository taskLatchRepository;

    @Resource
    private DAGInstanceRepository dagInstanceRepository;

    @Resource
    private MinIOService minIOService;


    @Override
    public Page<DAGTemplate> getTemplateList(Pageable pageable) {
        return dagTemplateRepository.findAll(pageable);
    }

    @Override
    public Page<DAGInstance> getInstanceList(Pageable pageable) {
        return dagInstanceRepository.findAll(pageable);
    }

    @Override
    public void saveTemplateByPack(LogicFlowPack pack) {
        dagTemplateRepository.save(pack.getTemplate());
    }


    @Override
    public DAGInstance createLogicFlowInstanceByTemplateId(Long templateId) {
        DAGTemplate template = dagTemplateRepository.getById(templateId);
        if (template.getStatus() != StatusConstant.NORMAL) {
            throw new DAGException("该流程状态不允许启动实例");
        }
        minIOService.getTemplateStr(template);
        Pair<List<Edge>, List<DAGPointer>> pair = template.translateLogicFlowDAG(retryMaxCount);
        List<Edge> edges = pair.getFirst();
        List<DAGPointer> pointers = pair.getSecond();
        edgeRepository.saveAll(edges);
        dagPointerRepository.saveAll(pointers);
        List<TaskLatch> latches = TaskLatch.getLatch(edges);
        taskLatchRepository.saveAll(latches);
        Long instanceId = pointers.get(0).getInstanceId();
        DAGInstance instance = DAGInstance.builder()
                .instanceId(instanceId)
                .templateId(templateId)
                .templateName(template.getName())
                .space(template.getSpace())
                .version(template.getVersion())
                .status(StatusConstant.TEMP)
                .createTime(LocalDateTime.now())
                .build();
        dagInstanceRepository.save(instance);
        return instance;
    }

    @Override
    public List<Edge> findNextEdges(TaskKey taskKey) {
        return edgeRepository.findByInstanceIdAndFromTaskId(taskKey.getInstanceId(), taskKey.getTaskId());
    }

    @Override
    public void deletePointer(TaskKey taskKey) {
        dagPointerRepository.deleteByInstanceIdAndTaskId(taskKey.getInstanceId(), taskKey.getTaskId());
    }

    @Override
    public void saveReadyPointers(List<DAGPointer> pointers) {
        List<Long> list = pointers.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        taskLatchRepository.decreaseCount(list);
        Set<Long> unreadyTask = taskLatchRepository.getUnreadyTask(list);
        List<DAGPointer> readyTask = pointers.stream().filter(p -> !unreadyTask.contains(p.getTaskId()))
                .collect(Collectors.toList());
        List<Long> readyIds = readyTask.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        dagPointerRepository.saveAll(readyTask);
        taskLatchRepository.deleteLatchByIds(readyIds);
    }


    @Override
    public void tryFinishInstance(Long instanceId) {
        Integer instancePointCount = dagPointerRepository.getInstancePointCount(instanceId);
        if (instancePointCount < 1) {
            dagInstanceRepository.finishInstance(instanceId, new Date(), StatusConstant.FINISHED);
        }
    }

    @Override
    public void updateInstanceStatus(Long instanceId, Integer status) {
        dagInstanceRepository.updateInstanceStatus(instanceId, status);
    }

    @Override
    public void updatePointer(DAGPointer pointer) {
        dagPointerRepository.updateCount(pointer.getInstanceId(), pointer.getTaskId());
    }

    public List<DAGPointer> getPointers(int limit) {
        return dagPointerRepository.getPointers(limit);
    }

    @Transactional(transactionManager = "transactionManagerDAG")
    @Override
    public void updateTemplateStatus(Long templateId, int status) {
        dagTemplateRepository.updateTemplateStatus(templateId, status);
    }
}