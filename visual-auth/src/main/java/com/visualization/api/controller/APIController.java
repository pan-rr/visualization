package com.visualization.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.api.handler.AuthMessageHandler;
import com.visualization.service.UserService;
import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageRequest;
import com.visualization.auth.model.AuthRequest;
import com.visualization.auth.model.AuthResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    @Resource
    private UserService userService;

    @Resource
    private AuthMessageHandler messageHandler;

    @PostMapping("/checkToken")
    AuthResponse checkToken(@RequestBody AuthRequest authRequest) {
        String token = StpUtil.getTokenValue();
        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setTokenLegal(StpUtil.isLogin());
        if (res.isTokenLegal()) {
            res.setTimeout(StpUtil.getTokenTimeout());
            messageHandler.publishLoginMessage(res.getToken(), res.getTimeout());
        }
        return res;
    }

    @PostMapping("/checkPermission")
    AuthResponse checkPermission(@RequestBody AuthRequest authRequest) {
        AuthResponse res = new AuthResponse();
        res.setPermissionLegal(StpUtil.hasPermission(authRequest.getPermission()));
        return res;
    }

    @PostMapping("/checkRole")
    AuthResponse checkRole(@RequestBody AuthRequest authRequest) {
        AuthResponse res = new AuthResponse();
        res.setRoleLegal(StpUtil.hasRole(authRequest.getRole()));
        return res;
    }

    @PostMapping("/fetchMessage")
    List<AuthMessage> fetchMessage(@RequestBody AuthMessageRequest request) {
        return messageHandler.fetchMessage(request);
    }

}
