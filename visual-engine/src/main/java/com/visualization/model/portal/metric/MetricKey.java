package com.visualization.model.portal.metric;

import lombok.Getter;

@Getter
public enum MetricKey {
    mode("mode"),
    space("space"),
    templateId("template_id"),
    instanceId("instance_id"),
    taskId("task_id"),
    time("time"),
    value("value");

    private final String alias;

    MetricKey(String alias){
        this.alias = alias;
    }

    public static MetricKey getMode(String mode){
        if (space.name().equals(mode)) {
            return space;
        }
        if (templateId.name().equals(mode)) {
            return templateId;
        }
        if (instanceId.name().equals(mode)) {
            return instanceId;
        }
        throw new NullPointerException("mode 只能为space、templateId、instanceId");
    }
}
