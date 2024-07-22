package com.visualisation.service.impl;

import com.google.gson.Gson;
import com.visualisation.exception.DAGException;
import com.visualisation.constant.StatusConstant;
import com.visualisation.model.dag.*;
import com.visualisation.model.dag.logicflow.LogicFlowPack;
import com.visualisation.repository.dag.*;
import com.visualisation.service.DAGService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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


    @Override
    public void saveTemplate(DAGTemplate dagTemplate) {
        dagTemplate.setStatus(StatusConstant.NORMAL);
        List<Node> list = dagTemplate.getList();
        dagTemplate.fillNodeId(list, 0);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        dagTemplate.setGraph(json);
        dagTemplate.translateDAG(retryMaxCount);
        dagTemplateRepository.save(dagTemplate);
    }

    @Override
    public Page<DAGTemplate> getTemplateList(Pageable pageable) {
        return dagTemplateRepository.findAll(pageable);
    }

    @Override
    public void saveTemplateByPack(LogicFlowPack pack) {
        dagTemplateRepository.save(pack.getTemplate());
    }

    @Override
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        DAGTemplate template = dagTemplateRepository.getById(templateId);
        if (template.getStatus() != StatusConstant.NORMAL) {
            throw new DAGException("该流程状态不允许启动实例");
        }
        Pair<List<Edge>, List<DAGPointer>> pair = template.translateDAG(retryMaxCount);
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
                .version(template.getVersion())
                .status(StatusConstant.NORMAL)
                .build();
        dagInstanceRepository.save(instance);
        return instance;
    }


    @Override
    public DAGInstance createLogicFlowInstanceByTemplateId(Long templateId) {
        DAGTemplate template = dagTemplateRepository.getById(templateId);
        if (template.getStatus() != StatusConstant.NORMAL) {
            throw new DAGException("该流程状态不允许启动实例");
        }
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
                .version(template.getVersion())
                .status(StatusConstant.NORMAL)
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
            dagInstanceRepository.updateInstanceStatus(instanceId, StatusConstant.FINISHED);
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
}
