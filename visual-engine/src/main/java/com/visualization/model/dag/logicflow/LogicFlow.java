package com.visualization.model.dag.logicflow;

import com.visualization.constant.StatusConstant;
import com.visualization.exception.DAGException;
import com.visualization.jpa.SnowIdWorker;
import com.visualization.model.dag.*;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.dag.db.Edge;
import com.visualization.model.dag.db.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlow {

    private List<LogicFlowNode> nodes;
    private List<LogicFlowEdge> edges;
    private Map<String, String> idMap = new HashMap<>();

    public List<Task> getTasks() {
        List<Task> res = new ArrayList<>(nodes.size());
        for (LogicFlowNode node : nodes) {
            Task task = node.convertTask();
            idMap.put(node.getId(), String.valueOf(task.getTaskId()));
            res.add(task);
        }
        overrideId();
        return res;
    }

    public void overrideId() {
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


    public Pair<List<Edge>, List<DAGPointer>> translateDAG(DAGTemplate template, Integer retryMaxCount) {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        long instanceId = snowIdWorker.nextId();
        Set<String> roots = nodes.stream().map(LogicFlowNode::getId).collect(Collectors.toSet());
        List<String[]> data = new ArrayList<>();
        edges.forEach(e -> {
            data.add(new String[]{e.getSourceNodeId(), e.getTargetNodeId()});
            roots.remove(e.getTargetNodeId());
        });
        List<DAGPointer> dagPointers = roots.stream().map(nodeId -> DAGPointer.builder()
                .instanceId(instanceId)
                .count(0)
                .retryMaxCount(retryMaxCount)
                .taskId(Long.valueOf(nodeId))
                .templateId(template.getTemplateId())
                .space(template.getSpace())
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
