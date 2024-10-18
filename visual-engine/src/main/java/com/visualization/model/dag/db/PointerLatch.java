package com.visualization.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(value = PointerId.class)
@Table(name = "t_pointer_latch")
public class PointerLatch implements Serializable {

    @Id
    private Long instanceId;

    @Id
    private Long taskId;

    // 剩余前置任务数
    private Integer count;


    public static List<PointerLatch> getLatch(List<Edge> edges, Long instanceId) {
        Map<Long, Integer> waitMap = new HashMap<>();
        edges.forEach(e -> {
            if (!Objects.equals(e.getFromTaskId(), e.getToTaskId())) {
                waitMap.compute(e.getToTaskId(), (k, v) -> {
                    if (v == null) v = 1;
                    else v++;
                    return v;
                });
            }
        });
        waitMap.remove(null);
        Set<Long> waitKey = waitMap.keySet();
        return waitKey.stream().map(id -> PointerLatch.builder()
                .instanceId(instanceId)
                .taskId(id)
                .count(waitMap.get(id))
                .build()).collect(Collectors.toList());
    }

}
