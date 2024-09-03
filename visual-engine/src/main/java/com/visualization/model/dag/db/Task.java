package com.visualization.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_task")
public class Task implements Serializable {

    @Id
    private Long taskId;

    private String name;

    @Column(columnDefinition = "text")
    private String json;

    @Column
    private LocalDateTime createTime;

}
