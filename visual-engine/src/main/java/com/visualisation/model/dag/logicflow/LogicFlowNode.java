package com.visualisation.model.dag.logicflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlowNode {

    private String id;

    private String type;

    private String x;

    private String y;

    private Map<String,Object> properties;

    private Map<String,Object> text;
}
