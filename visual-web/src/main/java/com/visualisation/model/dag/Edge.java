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
@Table(name = "t_dag_edge")
@IdClass(value = EdgeId.class)
public class Edge implements Serializable {

    @Id
    private Long instanceId;
    @Id
    private Long fromTaskId;
    @Id
    private Long toTaskId;


}
