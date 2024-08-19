package com.visualization.manager;

import com.visualization.log.logger.VisualLogService;
import com.visualization.log.model.TimeLine;
import com.visualization.service.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class TimeLineManager {
    @Resource
    private VisualLogService visualLogService;

    @Resource
    private TaskService taskService;

    public List<TimeLine> getTimeLineByInstanceId(String instanceId) {
        return TimeLine.getTimeLines(instanceId, visualLogService, taskService);
    }
}
