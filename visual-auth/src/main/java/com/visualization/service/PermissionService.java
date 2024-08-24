package com.visualization.service;

import com.visualization.model.db.SystemPermission;
import com.visualization.model.db.SystemUserPermission;

public interface PermissionService {

    void createPermission(SystemPermission permission);

    void grantPermissionToUser(SystemUserPermission systemUserPermission);
}
