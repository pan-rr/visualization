package com.visualisation.controller;

import com.visualisation.Commander;
import com.visualisation.model.dag.Task;
import com.visualisation.model.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/single")
public class TaskController {

    @Resource
    private Commander commander;

    @PostMapping("/executeTask")
    public Response<String> executeTask(@RequestBody Task task) {
        commander.commandWithJsonString(task.getJson());
        return new Response<>("ok");
    }
}
