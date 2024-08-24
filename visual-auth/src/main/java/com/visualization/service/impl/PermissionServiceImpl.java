package com.visualization.service.impl;

import com.visualization.mapper.PermissionMapper;
import com.visualization.mapper.UserPermissionMapper;
import com.visualization.model.db.SystemPermission;
import com.visualization.model.db.SystemUserPermission;
import com.visualization.service.PermissionService;
import com.visualization.utils.SnowIdUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Override
    public void createPermission(SystemPermission permission) {
        permission.setPermissionId(SnowIdUtil.generateId());
        permissionMapper.insert(permission);
    }

    @Override
    public void grantPermissionToUser(SystemUserPermission systemUserPermission) {
        userPermissionMapper.insert(systemUserPermission);
    }
}
