package com.visualization.config;

import cn.dev33.satoken.stp.StpInterface;
import com.visualization.enums.RoleEnum;
import com.visualization.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
public class SaFilterConfig implements StpInterface {

    @Resource
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return userService.getPermission((Long) o);
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        RoleEnum role = userService.getRole((Long) o);
        return Collections.singletonList(role.getRoleName());
    }
}
