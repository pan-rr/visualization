package com.visualisation.controller;

import com.visualisation.manager.DAGManager;
import com.visualisation.model.Response;
import com.visualisation.model.dag.DAGInstance;
import com.visualisation.model.dag.DAGPointer;
import com.visualisation.model.dag.DAGTemplate;
import com.visualisation.model.dag.Task;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dag")
public class DagController {

    @Resource
    private DAGManager dagManager;

    @PostMapping("/createProcessTemplate")
    public Response<Object> createProcessTemplate(@RequestBody DAGTemplate dagTemplate) {
        dagManager.saveTemplate(dagTemplate);
        return Response.builder().result(Boolean.TRUE).build();
    }

    @PostMapping("/saveTask")
    public Response<Object> createProcessTemplate(@RequestBody Task task) {
        dagManager.saveTask(task);
        return Response.builder().result(Boolean.TRUE).build();
    }

    @GetMapping("/createInstance")
    public Response<Object> createInstance(@RequestParam("templateId") Long templateId) {
        DAGInstance instance = dagManager.createInstanceByTemplateId(templateId);
        return Response.builder().result(instance).build();
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
