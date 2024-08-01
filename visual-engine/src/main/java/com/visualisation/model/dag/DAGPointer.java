package com.visualisation.model.dag;

import com.google.gson.Gson;
import com.visualisation.constant.MinIOConstant;
import com.visualisation.constant.ViewConstant;
import com.visualisation.exception.DAGException;
import com.visualisation.service.TaskService;
import com.visualisation.view.VisualGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_pointer")
@IdClass(value = PointerId.class)
public class DAGPointer implements Serializable {
    @Id
    private Long instanceId;
    @Id
    private Long taskId;
    private Long templateId;
    private Integer status;
    private Integer retryMaxCount;
    private Integer count;
    private String space;

    public TaskKey generateTaskKey() {
        return TaskKey.builder()
                .instanceId(instanceId)
                .taskId(taskId)
                .build();
    }

    public VisualGraph buildGraph(TaskService taskService) {
        Task task = taskService.getTaskById(taskId);
        if (Objects.isNull(task)) {
            throw new DAGException("任务为空，id:" + taskId);
        }
        Gson gson = new Gson();
        VisualGraph visualGraph = gson.fromJson(task.getJson(), VisualGraph.class);
        rewriteFilePath(visualGraph);
        return visualGraph;
    }

    private String handleFilePath(Object suffix) {
        if (StringUtils.hasText(space)) {
            return space + "/" + suffix;
        }
        return MinIOConstant.PUBLIC + "/" + suffix;
    }

    private void rewriteFilePath(VisualGraph graph) {
        List<Map<String, Object>> input = graph.getInput();
        if (!CollectionUtils.isEmpty(input)) {
            for (Map<String, Object> conf : input) {
                conf.compute(ViewConstant.FILE_PATH, (k, v) -> {
                    if (v == null) return null;
                    return handleFilePath(v);
                });
            }
        }
        Map<String, Object> output = graph.getOutput();
        if (!CollectionUtils.isEmpty(output)) {
            output.compute(ViewConstant.FILE_PATH, (k, v) -> {
                if (v == null) return null;
                return handleFilePath(v);
            });
        }
    }
}
