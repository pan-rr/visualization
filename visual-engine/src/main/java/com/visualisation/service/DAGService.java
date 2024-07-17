package com.visualisation.service;

import com.visualisation.model.dag.*;

import java.util.List;

public interface DAGService {

    void saveTemplate(DAGTemplate dagTemplate);

    DAGInstance createInstanceByTemplateId(Long templateId);

    List<Edge> findNextEdges(TaskKey taskKey);

    void deletePointer(TaskKey taskKey);

    void saveReadyPointers(List<DAGPointer> pointers);

    void tryFinishInstance(Long instanceId);

    void updateInstanceStatus(Long instanceId, Integer status);

    void updatePointer(DAGPointer pointer);

    List<DAGPointer> getPointers(int limit);

}
