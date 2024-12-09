package com.visualization.stage;

import com.visualization.runtime.VContext;
import com.visualization.runtime.VStageContext;
import com.visualization.runtime.VStageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VETContext implements VContext {

    private VStageContext stageContext;

    private Long templateId;

    private Long instanceId;

    private Long taskId;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return stageContext.get(key, clazz);
    }

    @Override
    public void put(String key, Object value) {
        stageContext.put(key, value);
    }

    @Override
    public VStageStatus getStatus() {
        return stageContext.getStatus();
    }

    @Override
    public void setStatus(VStageStatus status) {
        stageContext.setStatus(status);
    }
}
