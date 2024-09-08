package com.visualization.model.dag.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
@Table(name = "t_task")
public class Task implements Serializable {

    @Id
    private Long taskId;

    private String name;

    @Column(columnDefinition = "text")
    private String json;

    @Column(columnDefinition = "datetime")
    private LocalDateTime createTime;

}
