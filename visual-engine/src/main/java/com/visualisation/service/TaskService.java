package com.visualisation.service;

import com.visualisation.model.dag.Task;

public interface TaskService {

    void saveTask(Task task);

    Task getTaskById(Long id);

}
