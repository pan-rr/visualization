package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.service.UserService;
import com.visualization.utils.BindingResultUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/tenant")
public class TenantController {


    @Resource
    private UserService userService;

    @PostMapping("/registerSubTenant")
    public Response registerSubTenant(@Valid @RequestBody PortalTenantUser portalTenantUser, BindingResult bindingResult) {
        Response error = BindingResultUtil.checkError(bindingResult);
        if (error != null) return error;
        boolean flag = userService.createSubTenant(portalTenantUser);
        return Response.success(flag ? "注册成功！" : "该账号已被注册或不存在对应父账号，请检查！");
    }

}
