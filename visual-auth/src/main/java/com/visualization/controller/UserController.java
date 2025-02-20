package com.visualization.controller;

import com.visualization.api.handler.AuthMessageHandler;
import com.visualization.model.Response;
import com.visualization.model.api.ChangePassword;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.BindingResultUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    @Resource
    private AuthMessageHandler authMessageHandler;

    @PostMapping("/register")
    public Response register(@Valid @RequestBody SystemUser systemUser, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        boolean flag = userService.createUser(systemUser);
        return flag ? Response.success("注册成功！") : Response.error("创建失败，oa重复！");
    }

    @PostMapping("/changePassword")
    public Response changePassword(@RequestBody ChangePassword request){
        userService.changePassword(request);
        return Response.success("修改密码成功！");
    }

    @PostMapping("/login")
    public Response login(@RequestBody SystemUser systemUser) {
        UserInfo info = userService.login(systemUser);
        authMessageHandler.publishLoginMessage(info.getToken(), 1800L);
        return Response.success(info);
    }

    @GetMapping("/logout")
    public Response logout() {
        userService.logout();
        return Response.success("登出成功！");
    }

    @GetMapping("/getUserTenantPermission")
    public Response getUserTenantPermission(@RequestParam("tenantId") String tenantId) {
        return Response.success(userService.getUserTenantPermission(tenantId));
    }
}
