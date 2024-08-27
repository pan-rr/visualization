package com.visualization.api;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class APIController {

    @Resource
    private UserService userService;

    @GetMapping("/checkLogin")
    public Boolean checkLogin() {
        return StpUtil.isLogin();
    }

}
