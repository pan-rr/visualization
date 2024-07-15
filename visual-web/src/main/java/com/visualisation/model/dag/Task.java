package com.visualisation.model.dag;

import com.visualisation.constant.StatusConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_task")
@IdClass(value = TaskId.class)
public class Task implements Serializable {

    @Id
    private Long instanceId;

    @Id
    private Long taskId;

    private String name;

    @Column(columnDefinition = "text")
    private String json;

    private Integer status = StatusConstant.NORMAL;

}
