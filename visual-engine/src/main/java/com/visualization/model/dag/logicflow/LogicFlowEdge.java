package com.visualization.model.dag.logicflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlowEdge {
    private String id;
    private String type;
    private String sourceNodeId;
    private String targetNodeId;
    private Map<String,Object> startPoint;
    private Map<String,Object> endPoint;
    private Map<String,Object> properties;
    private List<Map<String,Object>> pointsList;
}