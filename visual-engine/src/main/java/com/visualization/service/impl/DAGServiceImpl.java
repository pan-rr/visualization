package com.visualization.service.impl;

import com.visualization.enums.DAGPriority;
import com.visualization.enums.Status;
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
import com.visualization.stage.VisualStageContextService;
import com.visualization.utils.PageUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple3;

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
    private PointerLatchRepository pointerLatchRepository;

    @Resource
    private DAGInstanceRepository dagInstanceRepository;

    @Resource
    private DAGDataSourceRepository dagDataSourceRepository;

    @Resource
    private MinIOService minIOService;

    @Resource
    private RedisService redisService;

    @Resource
    private VisualStageContextService visualStageContextService;


    @Override
    public Page<DAGTemplate> getTemplateList(PageParam parameter) {
        Pair<Specification<DAGTemplate>, PageRequest> pair = PageUtil.buidlRequestPair(parameter, DAGTemplate.class);
        return dagTemplateRepository.findAll(pair.getFirst(), pair.getSecond());
    }

    @Override
    public Page<DAGInstance> getInstanceList(PageParam parameter) {
        Pair<Specification<DAGInstance>, PageRequest> pair = PageUtil.buidlRequestPair(parameter, DAGInstance.class);
        return dagInstanceRepository.findAll(pair.getFirst(), pair.getSecond());
    }

    @Override
    public void saveTemplateByPack(LogicFlowPack pack) {
        dagTemplateRepository.save(pack.getTemplate());
    }


    @Override
    public DAGInstance createInstanceByTemplateId(Long templateId) {
        DAGTemplate template = getExecutableTemplate(templateId);
        minIOService.getTemplateStr(template);
        Tuple3<List<Edge>, List<DAGPointer>, Long> tuple = template.translateLogicFlowDAG();
        List<Edge> edges = tuple.getT1();
        List<DAGPointer> pointers = tuple.getT2();
        edgeRepository.saveAll(edges);
        dagPointerRepository.saveAll(pointers);
        Long instanceId = tuple.getT3();
        List<PointerLatch> latches = PointerLatch.getLatch(edges, instanceId);
        pointerLatchRepository.saveAll(latches);
        DAGInstance instance = DAGInstance.builder()
                .instanceId(instanceId)
                .templateId(templateId)
                .templateName(template.getName())
                .space(template.getSpace())
                .version(template.getVersion())
                .status(Status.NEW.getStatus())
                .createTime(LocalDateTime.now())
                .unfinishedTaskCount(template.getTotalTaskCount())
                .build();
        dagInstanceRepository.save(instance);
        visualStageContextService.createContext(InstanceContext.initContext(instance, template));
        return instance;
    }

    @Override
    public DAGInstance getInstance(Long instanceId) {
        return dagInstanceRepository.getById(instanceId);
    }

    @Override
    public List<Edge> findNextEdges(DAGPointer pointer) {
        return edgeRepository.findByInstanceIdAndFromTaskId(pointer.getInstanceId(), pointer.getTaskId());
    }

    @Override
    public void deletePointer(DAGPointer pointer) {
        dagPointerRepository.deleteByInstanceIdAndTaskId(pointer.getInstanceId(), pointer.getTaskId());
    }

    @Override
    public void makeSurePointerConfigMatchTemplate(DAGPointer pointer, DAGTemplate template) {
        boolean flag = pointer.matchTemplateConfig(template);
        if (!flag) {
            dagPointerRepository.save(pointer);
        }
    }

    @Override
    public List<Long> saveReadyPointers(List<DAGPointer> pointers, Long instanceId) {
        List<Long> list = pointers.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        pointerLatchRepository.decreaseCount(list, instanceId);
        Set<Long> readyTask = pointerLatchRepository.getReadyTask(instanceId);
        List<DAGPointer> readyPointers = pointers.stream().filter(p -> readyTask.contains(p.getTaskId()))
                .collect(Collectors.toList());
        List<Long> readyIds = readyPointers.stream().map(DAGPointer::getTaskId).collect(Collectors.toList());
        dagPointerRepository.saveAll(readyPointers);
        pointerLatchRepository.removeReadyLatch(readyIds, instanceId);
        return readyIds;
    }


    @Override
    public void tryFinishInstance(Long instanceId) {
        dagInstanceRepository.decreaseUnfinishedTaskCount(instanceId);
        dagInstanceRepository.tryFinishInstance(instanceId, new Date(), Status.FINISHED.getStatus());
    }

    @Override
    public void updateInstanceStatus(Long instanceId, Integer status) {
        Integer affect = dagInstanceRepository.updateInstanceStatus(instanceId, status);
        if (affect == null || affect == 0) return;
        if (Status.USER_TERMINATE.getStatus().equals(status)) {
            dagPointerRepository.deleteByInstanceId(instanceId);
            edgeRepository.deleteByInstanceId(instanceId);
            pointerLatchRepository.deleteLatchByInstanceId(instanceId);
        }
    }

    @Override
    public void updatePointer(DAGPointer pointer) {
        dagPointerRepository.save(pointer);
    }

    public List<DAGPointer> getExecutablePointers(int limit) {
        int[] executablePointerStatus = Status.getExecutablePointerStatus();
        return dagPointerRepository.getExecutablePointers(limit, executablePointerStatus);
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
        Integer hashCount = dagDataSourceRepository.getSpaceHashCount(dataSource.getConfigHash(), dataSource.getSpace());
        if (hashCount > 0) {
            throw new DAGException("该存储空间下已有一个高相似度的数据源!");
        }
        dagDataSourceRepository.save(dataSource);
    }

    @Override
    public void deleteDataSource(String id) {
        dagDataSourceRepository.deleteById(Long.valueOf(id));
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

    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    @Override
    public void changeTemplatePriority(Long templateId, Double priority) {
        DAGPriority.validatePriority(priority);
        dagTemplateRepository.changeTemplatePriority(templateId, priority);
    }

    @Transactional(transactionManager = "transactionManagerDAG", rollbackFor = Throwable.class)
    @Override
    public void changeTemplateRetryCount(Long templateId, Integer retryCount) {
        dagTemplateRepository.changeTemplateRetryCount(templateId, retryCount);
    }

    @Override
    public DAGTemplate getExecutableTemplate(Long templateId) {
        DAGTemplate template = Objects.requireNonNull(dagTemplateRepository.getById(templateId), "查无该模版！");
        if (!Objects.equals(template.getStatus(), Status.NORMAL.getStatus())) {
            throw new DAGException("该流程状态不允许执行操作！");
        }
        return template;
    }

    @Override
    public String getTemplateStr(Long templateId) {
        DAGTemplate template = getExecutableTemplate(templateId);
        minIOService.getTemplateStr(template);
        return template.getJson();
    }

    @Override
    public String getContext(Long instanceId) {
        return visualStageContextService.getById(instanceId).getContext();
    }
}
