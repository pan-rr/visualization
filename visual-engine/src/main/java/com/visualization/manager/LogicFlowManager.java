package com.visualization.manager;

import com.visualization.model.dag.logicflow.LogicGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LogicFlowManager {

    @Value("${visual.dag.stage.default-retry-count:5}")
    private Integer defaultRetryCount;

    @Value("${visual.dag.stage.default-priority:1.0}")
    private Double defaultPriority;

    @Resource
    private DAGManager dagManager;


    public void saveTemplate(LogicGraph graph) {
        graph.handleRetryCount(defaultRetryCount);
        graph.handlePriority(defaultPriority);
        dagManager.saveDAGPack(graph.getPack());
    }
}
