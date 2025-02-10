package com.visualization.model.portal.metric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnfinishedTask {
    private String id;
    private String template;
    private String instance;
    private String task;
    private Integer retry;
    private Integer maxRetry;
}
