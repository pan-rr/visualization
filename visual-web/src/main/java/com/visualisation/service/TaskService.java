package com.visualisation.service;

import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.TaskId;

public interface TaskService {

    void saveTask(Task task);

    Task getTaskById(TaskId id);

    void updateTaskStatus(Long instanceId, Long taskId, Integer status);
}
