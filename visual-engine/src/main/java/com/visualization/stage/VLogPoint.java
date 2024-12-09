package com.visualization.stage;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.visualization.runtime.VLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Measurement(name = "v_stage_log")
public class VLogPoint {

    @Column(name = "template_id",tag = true)
    private String templateId;

    @Column(name = "instance_id",tag = true)
    private String instanceId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "message")
    private String message;

    @Column(name = "theme")
    private Integer theme;

    @Column(timestamp = true)
    private Instant time;


    public static VLogPoint convert(VLog log){
        VETContext context = (VETContext) log.getContext();
        return VLogPoint.builder()
                .instanceId(context.getInstanceId().toString())
                .templateId(context.getTemplateId().toString())
                .taskId(context.getTaskId().toString())
                .message(log.getMessage())
                .theme(log.getTheme().getCode())
                .time(log.getTime())
                .build();
    }
}
