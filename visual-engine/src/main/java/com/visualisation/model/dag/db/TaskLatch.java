package com.visualisation.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_task_latch")
public class TaskLatch implements Serializable {

    @Id
    private Long taskId;

    // 剩余前置任务数
    private Integer count;


    public static List<TaskLatch> getLatch(List<Edge> edges) {
        Map<Long, Integer> waitMap = new HashMap<>();
        edges.forEach(e -> {
            if (!Objects.equals(e.getFromTaskId(), e.getToTaskId())){
                waitMap.compute(e.getToTaskId(), (k, v) -> {
                    if (v == null) v = 1;
                    else v++;
                    return v;
                });
            }
        });
        waitMap.remove(null);
        Set<Long> waitKey = waitMap.keySet();
        return waitKey.stream().map(id -> TaskLatch.builder()
                .taskId(id)
                .count(waitMap.get(id))
                .build()).collect(Collectors.toList());
    }

}
