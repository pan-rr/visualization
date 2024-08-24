package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.db.SystemResource;
import com.visualization.service.ResourceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Resource
    private ResourceService resourceService;

    @PostMapping("/createResource")
    public Response createResource(SystemResource systemResource){
        resourceService.createResource(systemResource);
        return Response.success("创建成功！");
    }
}
