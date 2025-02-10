package com.visualization.model.portal.metric;

import com.google.gson.Gson;
import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.dag.logicflow.LogicFlow;
import com.visualization.model.dag.logicflow.LogicFlowNode;
import com.visualization.model.dag.logicflow.LogicGraph;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TemplateSummary {

    private Map<String, LogicGraph> map;

    private Map<String, String> taskMap;

    public static TemplateSummary getInstance() {
        TemplateSummary summary = new TemplateSummary();
        summary.map = new HashMap<>();
        summary.taskMap = new HashMap<>();
        return summary;
    }

    public static TemplateSummary getInstance(List<DAGTemplate> templates, Map<String, String> templateMap) {
        TemplateSummary summary = getInstance();
        Gson gson = new Gson();
        for (DAGTemplate template : templates) {
            String id = template.getTemplateId().toString();
            String json = templateMap.get(id);
            if (Objects.isNull(json)) continue;
            LogicGraph build = LogicGraph.builder()
                    .space(template.getSpace())
                    .retryCount(template.getRetryCount())
                    .priority(template.getPriority())
                    .name(template.getName())
                    .logicFlow(gson.fromJson(json, LogicFlow.class)).build();
            summary.map.put(id, build);
        }
        summary.initTaskMap();
        return summary;
    }

    private void initTaskMap() {
        Collection<LogicGraph> values = map.values();
        for (LogicGraph value : values) {
            List<LogicFlowNode> nodes = value.getLogicFlow().getNodes();
            for (LogicFlowNode node : nodes) {
                taskMap.put(node.getId(), node.getText().get(MetricKey.value.name()).toString());
            }
        }
    }

    public String getTaskName(String taskId, String defaultName) {
        return taskMap.getOrDefault(taskId, defaultName);
    }

    private LogicFlowNode findNode(LogicGraph graph, String nodeId) {
        LogicFlow logicFlow = graph.getLogicFlow();
        List<LogicFlowNode> nodes = logicFlow.getNodes();
        for (LogicFlowNode node : nodes) {
            if (node.getId().equals(nodeId)) return node;
        }
        return null;
    }

    public List<Map<MetricKey, Object>> taskMetrics(List<TaskRecord> list) {
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>();
        return list.stream()
                .filter(r -> map.containsKey(r.getTemplateId()))
                .map(r -> {
                    LogicGraph graph = map.get(r.getTemplateId());
                    Map<MetricKey, Object> res = new HashMap<>();
                    res.put(MetricKey.templateId, graph.getName());
                    res.put(MetricKey.instanceId, r.getInstanceId());
                    LogicFlowNode node = findNode(graph, r.getTaskId());
                    res.put(MetricKey.taskId, node.getText().getOrDefault(MetricKey.value.name(), r.getTaskId()));
                    return res;
                }).collect(Collectors.toList());
    }

    public List<NameValueMetric> templateName(List<NameValueMetric> list) {
        if (CollectionUtils.isEmpty(list)) return new LinkedList<>();
        list.forEach(i -> {
            LogicGraph graph = map.get(i.getName());
            if (Objects.nonNull(graph)) {
                i.setName(graph.getName());
            }
        });
        return list;
    }


}
