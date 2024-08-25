package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.api.PortalResource;
import com.visualization.service.ResourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Resource
    private ResourceService resourceService;

    @PostMapping("/createResource")
    public Response createResource(@Validated @RequestBody PortalResource portalResource){
        resourceService.createResource(portalResource);
        return Response.success("创建成功！");
    }

    @PostMapping("/getResourceList")
    public Response getResourceList(@RequestBody PortalResource portalResource){
        return Response.success(resourceService.getResourceList(portalResource));
    }

    @GetMapping("/getResourceOption")
    public Response getResourceOption(@RequestParam("tenantId")String tenantId){
        return Response.success(resourceService.getResourceOptions(tenantId));
    }
}
