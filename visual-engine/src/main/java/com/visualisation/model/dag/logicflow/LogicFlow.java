package com.visualisation.model.dag.logicflow;

import com.visualisation.constant.StatusConstant;
import com.visualisation.exception.DAGException;
import com.visualisation.jpa.SnowIdWorker;
import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.DAGValidator;
import com.visualisation.model.dag.Edge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlow {

    private List<LogicFlowNode> nodes;
    private List<LogicFlowEdge> edges;

    public void overrideId(Map<String, String> idMap) {
        nodes.forEach(node -> node.setId(idMap.get(node.getId())));
        edges.forEach(e -> {
            e.setSourceNodeId(idMap.get(e.getSourceNodeId()));
            e.setTargetNodeId(idMap.get(e.getTargetNodeId()));
        });
    }

    public void validateDAG() {
        Set<String> roots = nodes.stream().map(LogicFlowNode::getId).collect(Collectors.toSet());
        List<String[]> data = new ArrayList<>();
        edges.forEach(e -> {
            data.add(new String[]{e.getSourceNodeId(), e.getTargetNodeId()});
            roots.remove(e.getTargetNodeId());
        });
        DAGValidator<String> validator = new DAGValidator<>(data);
        if (!validator.validate()) {
            throw new DAGException("检测到流程有环！请检查并去掉造成环的边！");
        }
    }


    public Pair<List<Edge>, List<DAGPointer>> translateDAG(Integer retryMaxCount) {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        long instanceId = snowIdWorker.nextId();
        Set<String> roots = nodes.stream().map(LogicFlowNode::getId).collect(Collectors.toSet());
        List<String[]> data = new ArrayList<>();
        edges.forEach(e -> {
            data.add(new String[]{e.getSourceNodeId(), e.getTargetNodeId()});
            roots.remove(e.getTargetNodeId());
        });
        DAGValidator<String> validator = new DAGValidator<>(data);
        if (!validator.validate()) {
            throw new DAGException("流程出现环！请检查！");
        }
        List<DAGPointer> dagPointers = roots.stream().map(nodeId -> DAGPointer.builder()
                .instanceId(instanceId)
                .count(0)
                .retryMaxCount(retryMaxCount)
                .taskId(Long.valueOf(nodeId))
                .status(StatusConstant.NORMAL)
                .build()).collect(Collectors.toList());
        List<Edge> dagEdges = edges.stream().map(e -> Edge
                        .builder()
                        .instanceId(instanceId)
                        .fromTaskId(Long.valueOf(e.getSourceNodeId()))
                        .toTaskId(Long.valueOf(e.getTargetNodeId()))
                        .build())
                .collect(Collectors.toList());
        return Pair.of(dagEdges, dagPointers);
    }
}
