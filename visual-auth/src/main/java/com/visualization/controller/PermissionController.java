package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.db.SystemPermission;
import com.visualization.model.db.SystemUserPermission;
import com.visualization.service.PermissionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @PostMapping("/createPermission")
    public Response createPermission(@RequestBody SystemPermission systemPermission){
        permissionService.createPermission(systemPermission);
        return Response.success("创建成功!");
    }

    @PostMapping("/grantPermission")
    public Response grantPermission(@RequestBody SystemUserPermission systemUserPermission){
        permissionService.grantPermissionToUser(systemUserPermission);
        return Response.success("赋权成功！");
    }
}
