package com.visualisation.service;

import com.visualisation.model.dag.Task;

import java.util.List;

public interface TaskService {

    void saveTask(Task task);

    void saveTask(List<Task> tasks);

    Task getTaskById(Long id);

}
