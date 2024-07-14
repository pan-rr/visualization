package com.visualisation.model.dag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "t_dag_detail")
public class DAGDetail {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    private String fromId;
    private String toId;
    private Integer attemptMaxCount;
    private Integer leftCount;
}
