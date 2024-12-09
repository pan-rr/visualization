package com.visualization.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisualStageTimeLine {

    private String instanceId;
    private String taskId;
    private String taskName;
    private String message;
    private String time;

}
