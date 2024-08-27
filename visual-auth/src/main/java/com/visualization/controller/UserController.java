package com.visualization.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.constant.AuthConstant;
import com.visualization.model.Response;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.BindingResultUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response register(@Valid @RequestBody SystemUser systemUser, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        userService.createUser(systemUser);
        return Response.success("注册成功！");
    }

    @PostMapping("/registerSubTenant")
    public Response registerSubTenant(@Valid @RequestBody PortalTenantUser portalTenantUser, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        userService.createSubTenant(portalTenantUser);
        return Response.success("注册成功！");
    }

    @PostMapping("/login")
    public Response login(@RequestBody SystemUser systemUser, HttpServletResponse response) {
        UserInfo info = userService.login(systemUser);
        return Response.success(info);
    }

    @GetMapping("/logout")
    public Response logout(HttpServletResponse response) {
        StpUtil.logout();
        response.addHeader(AuthConstant.LOGOUT, AuthConstant.LOGOUT);
        return Response.success("登出成功！");
    }

    @GetMapping("/getUserTenantPermission")
    public Response getUserTenantPermission(@RequestParam("tenantId") String tenantId) {
        return Response.success(userService.getUserTenantPermission(tenantId));
    }
}
