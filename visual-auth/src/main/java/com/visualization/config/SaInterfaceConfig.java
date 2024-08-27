package com.visualization.config;

import cn.dev33.satoken.stp.StpInterface;
import com.visualization.service.UserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SaInterfaceConfig implements StpInterface {

    @Resource
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return userService.getPermission(NumberUtils.createLong(o.toString()));
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return userService.getRole(NumberUtils.createLong(o.toString()));
    }
}
