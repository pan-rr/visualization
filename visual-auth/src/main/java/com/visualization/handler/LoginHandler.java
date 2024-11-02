package com.visualization.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.enums.UserStatusEnum;
import com.visualization.exeception.AuthException;
import com.visualization.mapper.TenantMapper;
import com.visualization.mapper.UserMapper;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;
import com.visualization.utils.MD5Utils;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


@Builder
public class LoginHandler {

    private SystemUser user;

    private UserMapper userMapper;

    private TenantMapper tenantMapper;

    private UserInfo userInfo;

    private AuthPermissionHandler authPermissionHandler;

    public UserInfo handleLogin() {
        validate();
        createUserInfo();
        option();
        token();
        permission();
        return userInfo;
    }

    private void permission(){
        authPermissionHandler.loadPermission(user.getUserId());
    }

    private void createUserInfo() {
        userInfo = UserInfo.builder()
                .oa(user.getOa())
                .name(user.getUsername())
                .userId(String.valueOf(user.getUserId()))
                .build();
    }

    private void validate() {
        SystemUser dbUser = getNonNullUser(user.getOa());
        String password = MD5Utils.encode(user.getPassword());
        boolean flag = StringUtils.equals(password, dbUser.getPassword());
        if (Boolean.FALSE.equals(flag)) {
            throw new AuthException("账号密码不匹配！");
        } else if (UserStatusEnum.FORBIDDEN.getCode() == dbUser.getStatus()) {
            throw new AuthException("该账号被禁用！");
        }
        this.user = dbUser;
    }

    private SystemUser getNonNullUser(String oa) {
        SystemUser user = userMapper.selectOneByOA(oa);
        if (Objects.isNull(user)) {
            throw new AuthException("查无此账号！");
        }
        return user;
    }

    private void option() {
        TenantOptionHandler handler = TenantOptionHandler.builder().user(user).tenantMapper(tenantMapper).build();
        userInfo.setTenantOptions(handler.computeTenantOption());
        userInfo.computeOptions();
    }

    private void token() {
        StpUtil.login(user.getUserId());
        String tokenValue = StpUtil.getTokenValue();
        userInfo.setToken(tokenValue);
    }
}
