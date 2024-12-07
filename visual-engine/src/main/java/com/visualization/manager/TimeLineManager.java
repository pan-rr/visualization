package com.visualization.manager;

import com.visualization.stage.VisualStageTimeLine;
import com.visualization.service.TaskService;
import com.visualization.stage.VLogPoint;
import com.visualization.stage.VLogger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TimeLineManager {

    @Resource
    private TaskService taskService;

    @Resource
    private VLogger vLogger;

    public List<VisualStageTimeLine> getTimeLine(String instanceId) {
        List<VLogPoint> points = vLogger.getLogPointsByInstanceId(instanceId);
        List<Long> taskIds = points.stream().map(VLogPoint::getTaskId).map(Long::valueOf).collect(Collectors.toList());
        Map<String, String> taskNameMap = taskService.getTaskNameMap(taskIds);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd hh:mm:ss")
                .withLocale(Locale.CHINESE)
                .withZone(ZoneId.systemDefault());
        return points.stream().map(p -> VisualStageTimeLine.builder()
                .instanceId(String.valueOf(p.getInstanceId()))
                .taskId(String.valueOf(p.getTaskId()))
                .taskName(taskNameMap.get(p.getTaskId()))
                .message(p.getMessage())
                .time(formatter.format(p.getTime()))
                .build()).collect(Collectors.toList());
    }
}
