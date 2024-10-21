package com.visualization.service.impl;

import com.visualization.enums.UserStatusEnum;
import com.visualization.enums.UserTypeEnum;
import com.visualization.exeception.AuthException;
import com.visualization.handler.AuthResourceHandler;
import com.visualization.handler.LoginHandler;
import com.visualization.handler.RoleHandler;
import com.visualization.mapper.ResourceMapper;
import com.visualization.mapper.TenantMapper;
import com.visualization.mapper.UserMapper;
import com.visualization.model.api.AuthResource;
import com.visualization.model.api.ChangePassword;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemResource;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.MD5Utils;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private ResourceMapper resourceMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean createUser(SystemUser user) {
        SystemUser db = userMapper.selectOneByOA(user.getOa());
        if (Objects.nonNull(db))return false;
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
    public void createSubTenant(PortalTenantUser tenantUser) {
        SystemUser systemUser = tenantUser.buildUser();
        SystemTenant systemTenant = tenantUser.buildTenant();
        SystemTenant father = tenantMapper.selectById(tenantUser.getFatherId());
        if (Objects.isNull(father)) {
            throw new AuthException("不存在该父账号，请检查！");
        }
        systemTenant.setRootId(father.getRootId() == null ? father.getTenantId() : father.getRootId());
        userMapper.insert(systemUser);
        tenantMapper.insert(systemTenant);
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
                .build();
        return loginHandler.login();
    }

    @Override
    public List<String> getPermission(Long userId) {
        Set<Long> userResource = resourceMapper.selectUserResourceList(userId)
                .stream()
                .flatMap(resources -> Arrays.stream(resources.split(",")).map(Long::valueOf))
                .collect(Collectors.toSet());
        List<SystemResource> allResource = resourceMapper.selectTenantResourceByUserId(userId);
        return allResource.stream()
                .filter(i -> userResource.contains(i.getResourceId()))
                .map(SystemResource::computeResourceKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRole(Long userId) {
        RoleHandler handler = RoleHandler.builder()
                .id(userId)
                .tenantMapper(tenantMapper)
                .build();
        return handler.computeRole();
    }

    @Override
    public AuthResource getUserTenantPermission(String tenantId) {
        return AuthResourceHandler.builder().tenantId(tenantId).tenantMapper(tenantMapper).build().handle();
    }
}