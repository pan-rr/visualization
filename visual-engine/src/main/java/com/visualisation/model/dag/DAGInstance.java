package com.visualisation.model.dag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_instance")
public class DAGInstance implements Serializable {
    @Id
    private Long instanceId;
    private Long templateId;
    private LocalDateTime version;
    private Integer status;

}
