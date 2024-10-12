package com.visualization.model.dag.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.visualization.enums.StatusEnum;
import com.visualization.model.dag.logicflow.LogicFlow;
import com.visualization.model.portal.PortalDAGTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import reactor.util.function.Tuple3;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_template")
@EntityListeners(value = AuditingEntityListener.class)
public class DAGTemplate implements Serializable {
    @Id
    private Long templateId;
    private String name;
    @Transient
    private String json;
    private Integer status;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime version;

    private String space;

    private Integer retryCount;

    private Double priority;

    private Integer totalTaskCount;

    public Tuple3<List<Edge>, List<DAGPointer>,Long> translateLogicFlowDAG() {
        Gson gson = new Gson();
        LogicFlow logicFlow = gson.fromJson(json, LogicFlow.class);
        return logicFlow.translateDAG(this);
    }

    public PortalDAGTemplate convert() {
        return PortalDAGTemplate.builder()
                .templateId(String.valueOf(templateId))
                .graph(json)
                .name(name)
                .space(space)
                .status(StatusEnum.getStatusNameByStatus(status))
                .version(version)
                .build();
    }
}
