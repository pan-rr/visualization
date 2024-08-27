package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.api.PortalResource;
import com.visualization.service.ResourceService;
import com.visualization.utils.BindingResultUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Resource
    private ResourceService resourceService;

    @PostMapping("/createResource")
    public Response createResource(@Valid @RequestBody PortalResource portalResource, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        resourceService.createResource(portalResource);
        return Response.success("创建成功！");
    }

    @PostMapping("/getResourceList")
    public Response getResourceList(@RequestBody PortalResource portalResource) {
        return Response.success(resourceService.getResourceList(portalResource));
    }

    @GetMapping("/getResourceOption")
    public Response getResourceOption(@RequestParam("tenantId") String tenantId) {
        return Response.success(resourceService.getResourceOptions(tenantId));
    }
}
