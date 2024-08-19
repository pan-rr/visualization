package com.visualization.model.dag.logicflow;

import com.visualization.model.dag.db.DAGTemplate;
import com.visualization.model.dag.db.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlowPack {
    private DAGTemplate template;
    private List<Task> tasks;
    private List<String> draftTaskIds;

}
