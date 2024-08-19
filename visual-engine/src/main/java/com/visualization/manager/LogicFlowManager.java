package com.visualization.manager;

import com.visualization.model.dag.logicflow.LogicGraph;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LogicFlowManager {

    @Resource
    private DAGManager dagManager;


    public void saveTemplate(LogicGraph graph) {
        dagManager.saveDAGPack(graph.getPack());
    }
}
