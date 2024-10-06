package com.visualization.model.dag.db;

import com.visualization.enums.StatusEnum;
import com.visualization.model.portal.PortalDAGInstance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
    private String templateName;
    private LocalDateTime version;
    private Integer status;
    private String space;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    private Integer unfinishedTaskCount;

    public PortalDAGInstance convert() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return PortalDAGInstance.builder()
                .templateId(String.valueOf(templateId))
                .templateName(templateName)
                .instanceId(String.valueOf(instanceId))
                .space(space)
                .status(StatusEnum.getStatusNameByStatus(status))
                .createTime(Objects.nonNull(createTime) ? formatter.format(createTime) : null)
                .finishTime(Objects.nonNull(finishTime) ? formatter.format(finishTime) : null)
                .unfinishedTaskCount(unfinishedTaskCount)
                .build();

    }

}
