package com.visualization.controller;

import com.visualization.manager.DAGManager;
import com.visualization.model.Response;
import com.visualization.model.dag.db.DAGPointer;
import com.visualization.model.dag.db.Task;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dag")
public class DagController {

    @Resource
    private DAGManager dagManager;

    @PostMapping("/saveTask")
    public Response<Object> createProcessTemplate(@RequestBody Task task) {
        dagManager.saveTask(task);
        return Response.builder().result(Boolean.TRUE).build();
    }

    /**
     * 此接口只为手动调用，正常流程应该由程序调用
     *
     * @param pointer 当前待允许的任务节点
     * @return response
     */
    @PostMapping("/executeTaskByPointer")
    public Response<Object> executeTaskByPointer(@RequestBody DAGPointer pointer) {
        dagManager.executeTask(pointer);
        return Response.builder().result(Boolean.TRUE).build();
    }
}