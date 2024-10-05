package com.visualization.model.dag.db;

import com.visualization.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_pointer")
@IdClass(value = PointerId.class)
public class DAGPointer implements Serializable, Comparable<DAGPointer> {
    @Id
    private Long instanceId;
    @Id
    private Long taskId;
    private Long templateId;
    private Integer status;
    private Integer retryMaxCount;
    private Integer count;
    private String space;
    private Double priority;

    @Override
    public int compareTo(@NotNull DAGPointer o) {
        // 降序
        return Double.compare(o.priority, this.priority);
    }

    public TaskKey generateTaskKey() {
        return TaskKey.builder()
                .instanceId(instanceId)
                .taskId(taskId)
                .build();
    }


    public String computeLockKey() {
        return "visual_pointer_" + taskId;
    }

    public void fail() {
        this.count++;
        this.checkBlock();
    }

    public void checkBlock() {
        if (retryMaxCount < count) {
            this.status = StatusEnum.BLOCK.getStatus();
        }
    }
}
