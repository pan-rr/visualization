package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.api.GrantView;
import com.visualization.model.api.PortalPermission;
import com.visualization.model.api.PortalUserPermission;
import com.visualization.model.db.SystemPermission;
import com.visualization.service.PermissionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @PostMapping("/createPermission")
    public Response createPermission(@RequestBody SystemPermission systemPermission) {
        permissionService.createPermission(systemPermission);
        return Response.success("创建成功!");
    }

    @PostMapping("/grantPermission")
    public Response grantPermission(@RequestBody @Validated PortalUserPermission portalUserPermission) {
        permissionService.grantPermissionToUser(portalUserPermission);
        return Response.success("赋权成功！");
    }

    @PostMapping("/getPermissionList")
    public Response getPermissionList(@Validated @RequestBody PortalPermission permission) {
        return Response.success(permissionService.getPermissionList(permission));
    }

    @PostMapping("/getGrantViewList")
    public Response getGrantViewList(@RequestBody GrantView grantView) {
        return Response.success(permissionService.getGrantViewList(grantView));
    }

    @PostMapping("/retractPermission")
    public Response retractPermission(@RequestBody GrantView grantView) {
        permissionService.retractPermission(grantView);
        return Response.success("撤销权限成功！");
    }
}
