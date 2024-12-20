package com.visualization.service;

import com.visualization.model.dag.db.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {

    void saveTask(Task task);

    void saveTask(List<Task> tasks);

    Task getTaskById(Long id);

    Map<String,String> getTaskNameMap(List<Long> taskId);

}
