package com.visualisation.model.dag;

import com.google.gson.Gson;
import com.visualisation.DAGException;
import com.visualisation.jpa.SnowIdWorker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_template")
public class DAGTemplate implements Serializable {
    @Id
    @GeneratedValue(generator = "snowId")
    @GenericGenerator(name = "snowId", strategy = "com.visualisation.jpa.SnowIdGenerator")
    private Long id;
    private String name;
    private String graph;
    private Integer status;
    @Transient
    private List<Node> list;

    public void fillNodeId(List<Node> list) {
        if (!CollectionUtils.isEmpty(list)) {
            SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
            list.forEach(node -> {
                node.setId(snowIdWorker.nextId());
                fillNodeId(node.getNextList());
            });
        }
    }

    public Pair<List<Edge>, List<DAGPointer>> translateDAG(Integer retryMaxCount) {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        long instanceId = snowIdWorker.nextId();
        Gson gson = new Gson();
        Node[] nodes = gson.fromJson(graph, Node[].class);
        Set<Node> set = new HashSet<>();
        List<DAGPointer> pointers = new ArrayList<>(nodes.length);
        List<Edge> edges = new LinkedList<>();
        LinkedList<Node> q = new LinkedList<>();
        for (Node node : nodes) {
            q.offer(node);
            pointers.add(DAGPointer.builder()
                    .instanceId(instanceId)
                    .count(0)
                    .retryMaxCount(retryMaxCount)
                    .taskId(node.getId())
                    .build());
        }
        int cnt;
        List<Node> arr;
        Node node;
        while ((cnt = q.size()) > 0) {
            while (cnt-- > 0) {
                node = q.poll();
                if (!set.add(node)) {
                    throw new DAGException("节点重复，id：" + node.getId());
                }
                arr = node.getNextList();
                if (!CollectionUtils.isEmpty(arr)) {
                    for (Node next : arr) {
                        edges.add(Edge.builder()
                                .instanceId(instanceId)
                                .fromTaskId(node.getId())
                                .toTaskId(next.getId())
                                .build());
                        q.offer(next);
                    }
                }
            }
        }
        return Pair.of(edges, pointers);
    }


}
