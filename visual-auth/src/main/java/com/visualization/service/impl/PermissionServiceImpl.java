package com.visualization.service.impl;

import com.visualization.exeception.AuthException;
import com.visualization.mapper.GrantViewMapper;
import com.visualization.mapper.PermissionMapper;
import com.visualization.mapper.UserPermissionMapper;
import com.visualization.model.api.GrantView;
import com.visualization.model.api.PortalPermission;
import com.visualization.model.api.PortalUserPermission;
import com.visualization.model.db.SystemPermission;
import com.visualization.model.db.SystemUser;
import com.visualization.model.db.SystemUserPermission;
import com.visualization.service.PermissionService;
import com.visualization.service.UserService;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private UserService userService;

    @Resource
    private GrantViewMapper grantViewMapper;

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Override
    public void createPermission(SystemPermission permission) {
        permission.setPermissionId(SnowIdUtil.generateId());
        permissionMapper.insert(permission);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void grantPermissionToUser(PortalUserPermission portalUserPermission) {
        SystemUser systemUser = userService.getNonNullUser(portalUserPermission.getOa());
        SystemUserPermission build = SystemUserPermission.builder()
                .userId(systemUser.getUserId())
                .permissionId(Long.valueOf(portalUserPermission.getPermissionId()))
                .build();
        SystemUserPermission selectOne = userPermissionMapper.selectOne(build);
        if (Objects.nonNull(selectOne)) {
            throw new AuthException("该OA已被附上对应权限！");
        }
        userPermissionMapper.insertOne(build);
    }

    @Override
    public List<PortalPermission> getPermissionList(PortalPermission permission) {
        List<SystemPermission> systemPermissions = permissionMapper.selectList(permission.buildWrapper());
        return PortalPermission.covert(systemPermissions);
    }

    @Override
    public List<GrantView> getGrantViewList(GrantView grantView) {
        return grantViewMapper.selectGrantViewList(grantView);
    }

    @Override
    public void retractPermission(GrantView grantView) {
        SystemUser systemUser = userService.getNonNullUser(grantView.getOa());
        grantViewMapper.retractPermission(systemUser.getUserId(), Long.valueOf(grantView.getPermissionId()));
    }
}
