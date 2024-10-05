package com.visualization.model.portal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortalDAGInstance implements Serializable {
    private String instanceId;
    private String templateId;
    private String templateName;
    private String space;
    private String status;
    private String createTime;
    private String finishTime;
    private Integer unfinishedTaskCount;
}
