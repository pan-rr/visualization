package com.visualization.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "t_abnormal_dag_pointer")
@IdClass(value = PointerId.class)
public class AbnormalDAGPointer implements Serializable {
    @Id
    private Long instanceId;
    @Id
    private Long taskId;

    private Integer status;

    private String space;

    public static AbnormalDAGPointer convert(DAGPointer pointer){
        return AbnormalDAGPointer.builder()
                .instanceId(pointer.getInstanceId())
                .taskId(pointer.getTaskId())
                .space(pointer.getSpace())
                .status(pointer.getStatus())
                .build();
    }

}
