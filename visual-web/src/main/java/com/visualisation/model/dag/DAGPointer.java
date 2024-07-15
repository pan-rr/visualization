package com.visualisation.model.dag;

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
@Table(name = "t_dag_pointer")
@IdClass(value = PointerId.class)
public class DAGPointer implements Serializable {
    @Id
    private Long instanceId;
    @Id
    private Long taskId;
    private Integer status;
    private Integer retryMaxCount;
    private Integer count;
}
