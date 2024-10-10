package com.visualization.controller;

import com.visualization.auth.AuthHandler;
import com.visualization.auth.AuthHolder;
import com.visualization.auth.message.AuthMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/messageFeedback")
    public void messageFeedback(@RequestBody AuthMessage feedback){
        authHandler.handleMessageFeedback(feedback);
    }
}
