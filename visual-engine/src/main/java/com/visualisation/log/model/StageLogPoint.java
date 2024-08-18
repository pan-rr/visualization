package com.visualisation.log.model;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.visualisation.model.dag.db.DAGPointer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Measurement(name = "visual_stage_log")
public class StageLogPoint {

    @Column(name = "instance_id",tag = true)
    private String instanceId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "message")
    private String message;

    @Column(timestamp = true)
    private Instant time;

    public static StageLogPoint covert(VisualStageWrapper visualStageWrapper){
        DAGPointer pointer = visualStageWrapper.getPointer();
        return StageLogPoint.builder()
                .instanceId(pointer.getInstanceId().toString())
                .taskId((pointer.getTaskId()).toString())
                .message(visualStageWrapper.getStageExecuteMessage())
                .time(visualStageWrapper.getTime())
                .build();
    }

    public static List<StageLogPoint> covert(List<VisualStageWrapper> list){
        return list.stream().map(StageLogPoint::covert).collect(Collectors.toList());
    }

}
