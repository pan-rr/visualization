package com.visualization.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.api.handler.AuthMessageHandler;
import com.visualization.enums.UserStatusEnum;
import com.visualization.enums.UserTypeEnum;
import com.visualization.exeception.AuthException;
import com.visualization.handler.AuthPermissionHandler;
import com.visualization.handler.AuthResourceHandler;
import com.visualization.handler.LoginHandler;
import com.visualization.mapper.ResourceMapper;
import com.visualization.mapper.TenantMapper;
import com.visualization.mapper.UserMapper;
import com.visualization.model.api.AuthResource;
import com.visualization.model.api.ChangePassword;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.MD5Utils;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private ResourceMapper resourceMapper;

    @Resource
    private AuthPermissionHandler authPermissionHandler;

    @Resource
    private AuthMessageHandler authMessageHandler;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean createUser(SystemUser user) {
        SystemUser db = userMapper.selectOneByOA(user.getOa());
        if (Objects.nonNull(db)) return false;
        user.setPassword(MD5Utils.encode(user.getPassword()));
        user.setUserId(SnowIdUtil.generateId());
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setModifyTime(now);
        if (UserTypeEnum.isTenant(user.getUserType())) {
            SystemTenant tenant = SystemTenant.builder()
                    .tenantId(user.getUserId())
                    .tenantName(user.getUsername())
                    .build();
            tenantMapper.insert(tenant);
        }
        userMapper.insert(user);
        return true;
    }

    @Override
    public void changePassword(ChangePassword request) {
        String old = MD5Utils.encode(request.getOldPassword());
        String newPassword = MD5Utils.encode(request.getNewPassword());
        int affect = userMapper.updatePassword(request.getUserId(), old, newPassword);
        if (affect < 1) {
            throw new AuthException("修改密码失败！");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean createSubTenant(PortalTenantUser tenantUser) {
        SystemUser db = userMapper.selectOneByOA(tenantUser.getOa());
        if (Objects.nonNull(db)) return false;
        SystemTenant father = tenantMapper.selectById(tenantUser.getFatherId());
        if (Objects.isNull(father)) return false;
        Tuple2<SystemUser, SystemTenant> tuple2 = tenantUser.registerSubTenant();
        SystemUser systemUser = tuple2.getT1();
        SystemTenant systemTenant = tuple2.getT2();
        systemTenant.setRootId(father.getRootId() == null ? father.getTenantId() : father.getRootId());
        userMapper.insert(systemUser);
        tenantMapper.insert(systemTenant);
        return true;
    }

    @Override
    public SystemUser getNonNullUser(String oa) {
        SystemUser user = userMapper.selectOneByOA(oa);
        if (Objects.isNull(user)) {
            throw new AuthException("查无此账号！");
        }
        return user;
    }

    @Override
    public UserInfo login(SystemUser user) {
        LoginHandler loginHandler = LoginHandler.builder()
                .user(user)
                .userMapper(userMapper)
                .tenantMapper(tenantMapper)
                .authPermissionHandler(authPermissionHandler)
                .build();
        return loginHandler.handleLogin();
    }


    @Override
    public void logout() {
        authPermissionHandler.cleanPermission();
        authMessageHandler.publishLogoutMessage(StpUtil.getTokenValue());
        StpUtil.logout();

    }

    @Override
    public AuthResource getUserTenantPermission(String tenantId) {
        return AuthResourceHandler.builder().tenantId(tenantId).tenantMapper(tenantMapper).build().handle();
    }
}