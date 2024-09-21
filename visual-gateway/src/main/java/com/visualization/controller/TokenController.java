package com.visualization.controller;

import com.visualization.auth.AuthHandler;
import com.visualization.auth.AuthHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inline")
public class TokenController {

    @Resource
    private AuthHandler authHandler;

    @PostMapping("/getModifyTime")
    public Long getModifyTime(@RequestBody AuthHolder authHolder) {
        return authHandler.getModifyTime(authHolder);
    }

}
