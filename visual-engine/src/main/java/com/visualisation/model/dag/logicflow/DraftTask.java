package com.visualisation.model.dag.logicflow;

import com.visualisation.exception.DAGException;
import com.visualisation.jpa.SnowIdWorker;
import com.visualisation.model.dag.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_draft_task")
@EntityListeners(value = AuditingEntityListener.class)
public class DraftTask {

    @Id
    private String id;

    private String name;

    @Column(columnDefinition = "text")
    private String json;

    @LastModifiedDate
    private LocalDateTime modifyTime;

    public void validate() {
        if (StringUtils.isEmpty(name)) {
            throw new DAGException("名称不能为空");
        }
        if (StringUtils.isEmpty(json)) {
            throw new DAGException("JSON不能为空");
        }
    }

    public static List<Task> convert(List<DraftTask> list) {
        SnowIdWorker snowIdWorker = new SnowIdWorker(0, 0);
        int size = list.size();
        List<Task> res = new ArrayList<>(size);
        DraftTask draftTask;
        for (DraftTask task : list) {
            draftTask = task;
            res.add(Task.builder()
                    .taskId(snowIdWorker.nextId())
                    .name(draftTask.name)
                    .json(draftTask.json)
                    .build());
        }
        return res;
    }
}
