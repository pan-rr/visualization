package com.visualisation.service;

import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.Edge;
import com.visualisation.model.dag.TaskKey;

import java.util.List;

public interface DAGService {

    void saveTemplate(DAGTemplate dagTemplate);

    void createInstanceByTemplateId(Long templateId);

    List<Edge> findNextEdges(TaskKey taskKey);

    void deletePointer(TaskKey taskKey);

    void saveReadyPointers(List<DAGPointer> pointers);

    void tryFinishInstance(Long instanceId);

}
