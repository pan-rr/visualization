package com.visualization.model.dag.db;

import com.visualization.enums.StatusEnum;
import com.visualization.utils.FilePathUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_dag_pointer")
@IdClass(value = PointerId.class)
public class DAGPointer implements Serializable {
    @Id
    private Long instanceId;
    @Id
    private Long taskId;
    private Long templateId;
    private Integer status;
    private Integer retryMaxCount;
    private Integer count;
    private String space;

    public TaskKey generateTaskKey() {
        return TaskKey.builder()
                .instanceId(instanceId)
                .taskId(taskId)
                .build();
    }

    private String handleFilePath(Object suffix) {
        if (suffix == null) return null;
        String s = String.valueOf(suffix);
        if (s.contains("://")) return s;
        Map<String, Object> params = new HashMap<>();
        if (s.startsWith(FilePathUtil.SPACE_SHARE)) {
            params.put(FilePathUtil.SPACE_SHARE, true);
        }
        params.put("space", StringUtils.hasText(space) ? space : FilePathUtil.PUBLIC_SPACE);
        params.put("instanceId", instanceId);
        params.put("templateId", templateId);
        params.put("filePath", s);
        return FilePathUtil.getStoragePath(params);
    }



    public String computeLockKey(){
        return "visual_pointer_" + taskId;
    }

    public void fail(){
        this.count++;
        this.checkBlock();
    }
    public void checkBlock(){
        if (retryMaxCount < count) {
            this.status = StatusEnum.BLOCK.getStatus();
        }
    }
}
