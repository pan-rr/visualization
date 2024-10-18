package com.visualization.stage;

import com.visualization.enums.WorkerGroup;
import com.visualization.thread.Worker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualWorker {

    private Worker worker;

    private WorkerGroup group;

}
