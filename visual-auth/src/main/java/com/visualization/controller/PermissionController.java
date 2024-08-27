package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.api.GrantView;
import com.visualization.model.api.PortalPermission;
import com.visualization.model.api.PortalUserPermission;
import com.visualization.model.db.SystemPermission;
import com.visualization.service.PermissionService;
import com.visualization.utils.BindingResultUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @PostMapping("/createPermission")
    public Response createPermission(@Valid @RequestBody SystemPermission systemPermission, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        permissionService.createPermission(systemPermission);
        return Response.success("创建成功!");
    }

    @PostMapping("/grantPermission")
    public Response grantPermission(@RequestBody @Valid PortalUserPermission portalUserPermission, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        permissionService.grantPermissionToUser(portalUserPermission);
        return Response.success("赋权成功！");
    }

    @PostMapping("/getPermissionList")
    public Response getPermissionList(@RequestBody PortalPermission permission) {
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
