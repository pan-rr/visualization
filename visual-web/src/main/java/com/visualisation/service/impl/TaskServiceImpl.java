package com.visualisation.service.impl;

import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.TaskId;
import com.visualisation.repository.dag.TaskRepository;
import com.visualisation.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskRepository taskRepository;

    @Override
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public Task getTaskById(TaskId id) {
        return taskRepository.getById(id);
    }

    @Override
    public void updateTaskStatus(Long instanceId, Long taskId, Integer status) {
        taskRepository.updateStatusByInstanceIdAndTaskId(instanceId, taskId, status);
    }
}
