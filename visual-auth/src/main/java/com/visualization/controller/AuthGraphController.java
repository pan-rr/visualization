package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.service.AuthGraphService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("graph")
@RestController
public class AuthGraphController {

    @Resource
    private AuthGraphService authGraphService;

    @GetMapping("/getGraph")
    public Response getGraph(@RequestParam("pre") String pre, @RequestParam("level") int level) {
        return Response.success(authGraphService.getGraph(pre, level));
    }


}
