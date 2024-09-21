package com.visualization.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.api.handler.AuthMessageHandler;
import com.visualization.auth.enums.AccessType;
import com.visualization.auth.message.AuthMessage;
import com.visualization.auth.message.AuthMessageRequest;
import com.visualization.auth.model.AuthRequest;
import com.visualization.auth.model.AuthResponse;
import com.visualization.service.UserService;
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
    public AuthResponse checkToken(@RequestBody AuthRequest authRequest) {
        String token = StpUtil.getTokenValue();
        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setTokenLegal(StpUtil.isLogin());
        if (res.isTokenLegal()) {
            messageHandler.publishLoginMessage(res.getToken(), authRequest.getTimeout());
        }
        return res;
    }

    @PostMapping("/checkPermission")
    public AuthResponse checkPermission(@RequestBody AuthRequest authRequest) {
        AuthResponse res = new AuthResponse();
        res.setPermissionLegal(StpUtil.hasPermission(authRequest.getPermission()));
        return res;
    }

    @PostMapping("/checkRole")
    public AuthResponse checkRole(@RequestBody AuthRequest authRequest) {
        AuthResponse res = new AuthResponse();
        res.setRoleLegal(StpUtil.hasRole(authRequest.getRole()));
        return res;
    }

    @PostMapping("/fetchMessage")
    public List<AuthMessage> fetchMessage(@RequestBody AuthMessageRequest request) {
        return messageHandler.fetchMessage(request);
    }

    @GetMapping("/renewTimeout")
    public void renewTimeout(@RequestParam("timeout") Long timeout) {
        messageHandler.publishLoginMessage(StpUtil.getTokenValue(), timeout);
    }

    @GetMapping("/logout")
    public void logout() {
        messageHandler.publishLogoutMessage(StpUtil.getTokenValue());
        StpUtil.logout();
    }

    @PostMapping("/validatePermission")
    public Integer validatePermission(@RequestBody AuthRequest request) {
        String permission = request.getPermission();
        List<String> permissionList = StpUtil.getPermissionList(StpUtil.getLoginIdByToken(request.getToken()));
        return permissionList.contains(permission) ? AccessType.PERMIT.getType() : AccessType.DENY.getType();
    }
}
