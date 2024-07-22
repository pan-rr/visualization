package com.visualisation.service;

import com.visualisation.model.dag.*;
import com.visualisation.model.dag.logicflow.LogicFlowPack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DAGService {

    void saveTemplate(DAGTemplate dagTemplate);

    Page<DAGTemplate> getTemplateList(Pageable pageable);

    void saveTemplateByPack(LogicFlowPack pack);

    DAGInstance createInstanceByTemplateId(Long templateId);

    List<Edge> findNextEdges(TaskKey taskKey);

    void deletePointer(TaskKey taskKey);

    void saveReadyPointers(List<DAGPointer> pointers);

    void tryFinishInstance(Long instanceId);

    void updateInstanceStatus(Long instanceId, Integer status);

    void updatePointer(DAGPointer pointer);

    List<DAGPointer> getPointers(int limit);

    DAGInstance createLogicFlowInstanceByTemplateId(Long templateId);
}
