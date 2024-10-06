package com.visualization.service.impl;

import com.visualization.enums.StatusEnum;
import com.visualization.exception.DAGException;
import com.visualization.model.PageParam;
import com.visualization.model.dag.db.*;
import com.visualization.model.dag.logicflow.LogicFlowPack;
import com.visualization.model.portal.Option;
import com.visualization.model.portal.PortalDataSource;
import com.visualization.repository.dag.*;
import com.visualization.service.DAGService;
import com.visualization.service.MinIOService;
import com.visualization.service.RedisService;
import com.visualization.utils.PageUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DAGServiceImpl implements DAGService {

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
    private DAGDataSourceRepository dagDataSourceRepository;

    @Resource
    private MinIOService minIOService;

    @Resource
    private RedisService redisService;


    @Override
    public Page<DAGTemplate> getTemplateList(PageParam parameter) {
        Pair<Specification<DAGTemplate>, PageRequest> pair = PageUtil.convertNormalRequest(parameter, DAGTemplate.class);
        return dagTemplateRepository.findAll(pair.getFirst(), pair.getSecond());
    }

    @Override
    public Page<DAGInstance> getInstanceList(PageParam parameter) {
        Pair<Specification<DAGInstance>, PageRequest> pair = PageUtil.convertNormalRequest(parameter, DAGInstance.class);
        return dagInstanceRepository.findAll(pair.getFirst(), pair.getSecond());
    }

    @Override
    public void saveTemplateByPack(LogicFlowPack pack) {
        dagTemplateRepository.save(pack.getTemplate());
    }


    @Override
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        DAGTemplate template = dagTemplateRepository.getById(templateId);
        if (!Objects.equals(template.getStatus(), StatusEnum.NORMAL.getStatus())) {
            throw new DAGException("该流程状态不允许启动实例");
        }
        minIOService.getTemplateStr(template);
        Pair<List<Edge>, List<DAGPointer>> pair = template.translateLogicFlowDAG();
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
                .status(StatusEnum.NEW.getStatus())
                .createTime(LocalDateTime.now())
                .unfinishedTaskCount(template.getTotalTaskCount())
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
    public List<Long> saveReadyPointers(List<DAGPointer> pointers) {
        List<Long> list = pointers.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        taskLatchRepository.decreaseCount(list);
        Set<Long> unreadyTask = taskLatchRepository.getUnreadyTask(list);
        List<DAGPointer> readyTask = pointers.stream().filter(p -> !unreadyTask.contains(p.getTaskId()))
                .collect(Collectors.toList());
        List<Long> readyIds = readyTask.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        dagPointerRepository.saveAll(readyTask);
        taskLatchRepository.deleteLatchByIds(readyIds);
        return readyIds;
    }


    @Override
    public void tryFinishInstance(Long instanceId) {
        dagInstanceRepository.decreaseUnfinishedTaskCount(instanceId);
        dagInstanceRepository.tryFinishInstance(instanceId, new Date(), StatusEnum.FINISHED.getStatus());
    }

    @Override
    public void updateInstanceStatus(Long instanceId, Integer status) {
        dagInstanceRepository.updateInstanceStatus(instanceId, status);
    }

    @Override
    public void updatePointer(DAGPointer pointer) {
        dagPointerRepository.save(pointer);
    }

    public List<DAGPointer> getPointers(int limit) {
        return dagPointerRepository.getPointers(limit);
    }

    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    @Override
    public void updateTemplateStatus(Long templateId, int status) {
        dagTemplateRepository.updateTemplateStatus(templateId, status);
    }


    @Override
    public void deleteEdges(List<EdgeId> ids) {
        edgeRepository.deleteAllById(ids);
    }


    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    @Override
    public void saveDAGDataSource(PortalDataSource portalDataSource) {
        DAGDataSource dataSource = portalDataSource.convert();
        Integer hashCount = dagDataSourceRepository.getSpaceHashCount(dataSource.getHash(), dataSource.getSpace());
        if (hashCount > 0) {
            throw new DAGException("该存储空间下已有一个高相似度的数据源!");
        }
        dagDataSourceRepository.save(dataSource);
    }

    @Override
    public List<PortalDataSource> getDataSourceList(PortalDataSource portalDataSource) {
        Example<DAGDataSource> example = portalDataSource.buildExample();
        List<DAGDataSource> all = dagDataSourceRepository.findAll(example);
        return DAGDataSource.convert(all);
    }

    @Override
    public List<DAGDataSource> getListByIds(List<Long> ids) {
        return dagDataSourceRepository.findAllById(ids);
    }

    @Override
    public List<Option> getDataSourceOptions(String space) {
        List<DAGDataSourceProject> list = dagDataSourceRepository.getProject(space);
        return list.stream().map(i ->
                Option.builder()
                        .label(i.getName())
                        .value(i.getData_source_id())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public DAGTemplate getTemplateByPointer(DAGPointer pointer) {
        return dagTemplateRepository.getById(pointer.getTemplateId());
    }

    @Override
    public void changeTemplatePriority(Long templateId, Double delta) {
        dagTemplateRepository.changeTemplatePriority(templateId, delta);
    }
}
