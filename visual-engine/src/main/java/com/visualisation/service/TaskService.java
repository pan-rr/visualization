package com.visualisation.service;

import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.logicflow.DraftTask;

import java.util.List;

public interface TaskService {

    void saveTask(Task task);

    void saveTask(List<Task> tasks);

    void saveDraftTask(DraftTask draftTask);

    List<DraftTask> getDraftTaskListByIDList(List<String> ids);
    void deleteDraftTaskByIDList(List<String> ids);

    Task getTaskById(Long id);

}
