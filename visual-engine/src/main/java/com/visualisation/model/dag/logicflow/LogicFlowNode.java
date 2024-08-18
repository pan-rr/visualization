package com.visualisation.model.dag.logicflow;

import com.google.gson.Gson;
import com.visualisation.exception.DAGException;
import com.visualisation.model.dag.db.Task;
import com.visualisation.utils.SnowIdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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
        if (StringUtils.isEmpty(taskName) || Objects.isNull(json)) {
            throw new DAGException(taskName + "任务详情配置不完整，请检查！");
        }
        Gson gson = new Gson();
        String str = gson.toJson(json);
        return Task
                .builder()
                .taskId(SnowIdUtil.nextId())
                .name(taskName)
                .json(str)
                .build();
    }
}
