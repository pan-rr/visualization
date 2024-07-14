package com.visualisation.model.dag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "t_task")
public class Task {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(name = "id")
    private String id;

    private String name;

    @Column(columnDefinition = "text")
    private String json;

    private Long createTimestamp;

    private Long modifyTimestamp;


    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.json = task.json;
        this.createTimestamp = task.createTimestamp;
        this.modifyTimestamp = System.currentTimeMillis();
    }


}
