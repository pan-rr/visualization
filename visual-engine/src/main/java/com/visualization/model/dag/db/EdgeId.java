package com.visualization.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EdgeId implements Serializable {
    private Long instanceId;

    private Long fromTaskId;

    private Long toTaskId;

    public static List<EdgeId> computeId(DAGPointer previous, List<Long> stepIds) {
        return stepIds.stream().map(i ->
                EdgeId.builder()
                        .instanceId(previous.getInstanceId())
                        .fromTaskId(previous.getTaskId())
                        .toTaskId(i).build()).collect(Collectors.toList());
    }
}
