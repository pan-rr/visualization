package com.visualisation.service.impl;

import com.google.gson.Gson;
import com.visualisation.DAGException;
import com.visualisation.constant.StatusConstant;
import com.visualisation.model.dag.*;
import com.visualisation.repository.dag.EdgeRepository;
import com.visualisation.repository.dag.DAGPointerRepository;
import com.visualisation.repository.dag.DAGTemplateRepository;
import com.visualisation.service.DAGService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DAGServiceImpl implements DAGService {
    @Value("${dag.edge.retryMaxCount}")
    private Integer retryMaxCount;

    @Resource
    private EdgeRepository edgeRepository;

    @Resource
    private DAGTemplateRepository dagTemplateRepository;

    @Resource
    private DAGPointerRepository dagPointerRepository;


    @Override
    public void saveTemplate(DAGTemplate dagTemplate) {
        dagTemplate.setStatus(StatusConstant.NORMAL);
        List<Node> list = dagTemplate.getList();
        dagTemplate.fillNodeId(list);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        dagTemplate.setGraph(json);
        dagTemplate.translateDAG(retryMaxCount);
        dagTemplateRepository.save(dagTemplate);
    }

    @Override
    public void createInstanceByTemplateId(Long templateId) {
        DAGTemplate template = dagTemplateRepository.getById(templateId);
        if (template.getStatus() != StatusConstant.NORMAL) {
            throw new DAGException("该流程状态不允许启动实例");
        }
        Pair<List<Edge>, List<DAGPointer>> pair = template.translateDAG(retryMaxCount);
        List<Edge> edges = pair.getFirst();
        List<DAGPointer> pointers = pair.getSecond();
        edgeRepository.saveAll(edges);
        dagPointerRepository.saveAll(pointers);
    }

    @Override
    public List<Edge> findNextEdges(TaskId taskId) {
        return edgeRepository.findByInstanceIdAndFromTaskId(taskId.getInstanceId(), taskId.getTaskId());
    }

    @Override
    public void deletePointer(TaskId taskId) {
        dagPointerRepository.deleteByInstanceIdAndTaskId(taskId.getInstanceId(), taskId.getTaskId());
    }

    @Override
    public void savePointers(List<DAGPointer> pointers) {
        dagPointerRepository.saveAll(pointers);
    }


}
