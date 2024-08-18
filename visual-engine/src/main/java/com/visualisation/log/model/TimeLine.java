package com.visualisation.log.model;

import com.visualisation.log.logger.VisualLogService;
import com.visualisation.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeLine {

    private String instanceId;
    private String taskId;
    private String taskName;
    private String message;
    private String time;


    public static List<TimeLine> getTimeLines(String instanceId, VisualLogService visualLogService, TaskService taskService) {
        List<StageLogPoint> points = visualLogService.getLogPointsByInstanceId(instanceId);
        List<Long> taskIds = points.stream().map(p -> Long.valueOf(p.getTaskId())).collect(Collectors.toList());
        Map<String, String> taskNameMap = taskService.getTaskNameMap(taskIds);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd hh:mm:ss")
                .withLocale(Locale.CHINESE)
                .withZone(ZoneId.systemDefault());
        return points.stream().map(p -> TimeLine.builder()
                .instanceId(p.getInstanceId())
                .taskId(p.getTaskId())
                .taskName(taskNameMap.get(p.getTaskId()))
                .message(p.getMessage())
                .time(formatter.format(p.getTime()))
                .build()).collect(Collectors.toList());
    }

}
