package com.visualization.model.dag.logicflow;

import com.google.gson.Gson;
import com.visualization.exception.DAGException;
import com.visualization.model.dag.db.Task;
import com.visualization.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogicFlowNode {

    private String id;

    private String type;

    private String x;

    private String y;

    private Map<String, Object> properties;

    private Map<String, Object> text;


    public Task convertTask() {
        String taskName = String.valueOf(properties.get("text"));
        Object json = properties.get("json");
        boolean jsonConfig = properties.containsKey("json");
        if (StringUtils.isBlank(taskName) || (jsonConfig && Objects.isNull(json)) || CollectionUtils.isEmpty(properties)) {
            throw new DAGException(taskName + "任务详情配置不完整，请检查！");
        }
        Gson gson = new Gson();
        String str = jsonConfig ? gson.toJson(json) : gson.toJson(properties);
        return Task
                .builder()
                .taskId(SnowIdUtil.nextId())
                .name(taskName)
                .json(str)
                .createTime(LocalDateTime.now())
                .build();
    }
}
