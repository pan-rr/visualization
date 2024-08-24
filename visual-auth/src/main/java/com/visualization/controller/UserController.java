package com.visualization.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.model.Response;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response register(@Validated @RequestBody SystemUser systemUser) {
        userService.createUser(systemUser);
        return Response.success("注册成功！");
    }

    @PostMapping("/login")
    public Response login(@RequestBody SystemUser systemUser) {
        return Response.success(userService.login(systemUser));
    }

    @GetMapping("/logout")
    public Response logout() {
        StpUtil.logout();
        return Response.success("登出成功！");
    }
}
