package com.visualisation.model.dag;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.visualisation.constant.StatusConstant;
import com.visualisation.model.dag.logicflow.LogicFlow;
import com.visualisation.model.portal.PortalDAGTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_template")
@EntityListeners(value = AuditingEntityListener.class)
public class DAGTemplate implements Serializable {
    @Id
    @GeneratedValue(generator = "snowId")
    @GenericGenerator(name = "snowId", strategy = "com.visualisation.jpa.SnowIdGenerator")
    private Long templateId;
    private String name;
    @Column(columnDefinition = "text")
    private String graph;
    private Integer status;
//    @Transient
//    private List<Node> list;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime version;

//    public void fillNodeId(List<Node> list, int deepth) {
//        if (!CollectionUtils.isEmpty(list)) {
//            SnowIdWorker snowIdWorker = new SnowIdWorker(0, deepth++);
//            for (Node node : list) {
//                node.setId(snowIdWorker.nextId());
//                fillNodeId(node.getNextList(), deepth);
//            }
//        }
//    }

//    public Pair<List<Edge>, List<DAGPointer>> translateDAG(Integer retryMaxCount) {
//        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
//        long instanceId = snowIdWorker.nextId();
//        Gson gson = new Gson();
//        Node[] nodes = gson.fromJson(graph, Node[].class);
//        Set<Node> set = new HashSet<>();
//        List<DAGPointer> pointers = new ArrayList<>(nodes.length);
//        List<Edge> edges = new LinkedList<>();
//        LinkedList<Node> q = new LinkedList<>();
//        int cnt;
//        List<Node> arr;
//        for (Node node : nodes) {
//            arr = node.getNextList();
//            if (CollectionUtils.isEmpty(arr)) {
//                edges.add(Edge.builder()
//                        .instanceId(instanceId)
//                        .fromTaskId(node.getId())
//                        .toTaskId(node.getId())
//                        .build());
//            } else {
//                q.offer(node);
//            }
//            pointers.add(DAGPointer.builder()
//                    .instanceId(instanceId)
//                    .count(0)
//                    .retryMaxCount(retryMaxCount)
//                    .taskId(node.getId())
//                    .status(StatusConstant.NORMAL)
//                    .build());
//        }
//        Node node;
//        while ((cnt = q.size()) > 0) {
//            while (cnt-- > 0) {
//                node = q.poll();
//                if (!set.add(node)) {
//                    throw new DAGException("节点重复，id：" + node.getId());
//                }
//                arr = node.getNextList();
//                if (!CollectionUtils.isEmpty(arr)) {
//                    for (Node next : arr) {
//                        edges.add(Edge.builder()
//                                .instanceId(instanceId)
//                                .fromTaskId(node.getId())
//                                .toTaskId(next.getId())
//                                .build());
//                        q.offer(next);
//                    }
//                }
//            }
//        }
//        return Pair.of(edges, pointers);
//    }

    public Pair<List<Edge>, List<DAGPointer>> translateLogicFlowDAG(Integer retryMaxCount) {
        Gson gson = new Gson();
        LogicFlow logicFlow = gson.fromJson(graph, LogicFlow.class);
        return logicFlow.translateDAG(retryMaxCount);
    }

    public PortalDAGTemplate convert() {
        return PortalDAGTemplate.builder()
                .templateId(String.valueOf(templateId))
                .graph(graph)
                .name(name)
                .status(StatusConstant.getStatusName(status))
                .version(version)
                .build();
    }
}
