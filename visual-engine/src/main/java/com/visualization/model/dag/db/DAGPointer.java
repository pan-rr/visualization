package com.visualization.model.dag.db;

import com.visualization.enums.Status;
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
import java.util.Objects;

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


    public String computeStageName() {
        return "visual:stage:" + instanceId + ":" + taskId;
    }

    public void fail() {
        this.count++;
        this.checkReachThreshold();
    }

    public void checkReachThreshold() {
        if (retryMaxCount < count) {
            this.status = Status.BLOCK_FAIL_REACH_THRESHOLD.getStatus();
        }
    }

    public boolean matchTemplateConfig(DAGTemplate template) {
        boolean res = true;
        if (!Objects.equals(template.getRetryCount(), this.retryMaxCount)) {
            res = false;
            this.retryMaxCount = template.getRetryCount();
        }
        if (!Objects.equals(template.getPriority(), this.priority)) {
            res = false;
            this.priority = template.getPriority();
        }
        return res;
    }
}
