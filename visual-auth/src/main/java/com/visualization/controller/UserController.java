package com.visualization.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.constant.AuthConstant;
import com.visualization.model.Response;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${spring.sa-token.activity-timeout}")
    public String aliveTimeout;

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response register(@Validated @RequestBody SystemUser systemUser) {
        userService.createUser(systemUser);
        return Response.success("注册成功！");
    }

    @PostMapping("/registerSubTenant")
    public Response registerSubTenant(@Validated @RequestBody PortalTenantUser portalTenantUser) {
        userService.createSubTenant(portalTenantUser);
        return Response.success("注册成功！");
    }

    @PostMapping("/login")
    public Response login(@RequestBody SystemUser systemUser, HttpServletResponse response) {
        UserInfo info = userService.login(systemUser);
        response.setHeader(AuthConstant.NEW_TOKEN, info.getToken());
        response.setHeader(AuthConstant.TOKEN_ALIVE_TIME, aliveTimeout);
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
