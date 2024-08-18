package com.visualisation.manager;

import com.visualisation.log.logger.VisualLogService;
import com.visualisation.log.model.TimeLine;
import com.visualisation.service.TaskService;
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
