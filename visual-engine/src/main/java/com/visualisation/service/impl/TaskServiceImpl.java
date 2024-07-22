package com.visualisation.service.impl;

import com.visualisation.model.dag.Task;
import com.visualisation.model.dag.logicflow.DraftTask;
import com.visualisation.repository.dag.DraftTaskRepository;
import com.visualisation.repository.dag.TaskRepository;
import com.visualisation.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private DraftTaskRepository draftTaskRepository;

    @Override
    public void saveTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void saveTask(List<Task> tasks) {
        taskRepository.saveAll(tasks);
    }

    @Override
    public void saveDraftTask(DraftTask draftTask) {
        draftTask.validate();
        draftTaskRepository.save(draftTask);
    }

    @Override
    public List<DraftTask> getDraftTaskListByIDList(List<String> ids) {
        return draftTaskRepository.findAllById(ids);
    }

    @Override
    public void deleteDraftTaskByIDList(List<String> ids) {
        draftTaskRepository.deleteAllById(ids);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.getById(id);
    }

}
