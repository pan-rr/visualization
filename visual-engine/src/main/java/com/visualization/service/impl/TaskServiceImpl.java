package com.visualization.service.impl;

import com.visualization.model.dag.db.TaskProject;
import com.visualization.model.dag.db.Task;
import com.visualization.repository.dag.TaskRepository;
import com.visualization.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskRepository taskRepository;

    @Override
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void saveTask(List<Task> tasks) {
        taskRepository.saveAll(tasks);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.getById(id);
    }

    @Override
    public Map<String, String> getTaskNameMap(List<Long> taskId) {
        List<TaskProject> tasks = taskRepository.getTasks(taskId);
        Map<String, String> map = new HashMap<>();
        for (TaskProject task : tasks) {
            map.put(task.getTask_id().toString(), task.getName());
        }
        return map;
    }
}
