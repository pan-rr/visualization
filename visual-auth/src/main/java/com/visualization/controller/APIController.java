package com.visualization.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.model.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIController {

    @GetMapping("/checkLogin")
    public Response checkLogin() {
        return Response.success(StpUtil.isLogin());
    }
}
