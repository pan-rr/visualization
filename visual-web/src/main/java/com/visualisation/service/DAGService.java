package com.visualisation.service;

import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.Edge;
import com.visualisation.model.dag.TaskId;

import java.util.List;

public interface DAGService {

    void saveTemplate(DAGTemplate dagTemplate);

    void createInstanceByTemplateId(Long templateId);

    List<Edge> findNextEdges(TaskId taskId);

    void deletePointer(TaskId taskId);

    void savePointers(List<DAGPointer> pointers);

}
