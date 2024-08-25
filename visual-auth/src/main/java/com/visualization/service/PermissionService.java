package com.visualization.service;

import com.visualization.model.api.GrantView;
import com.visualization.model.api.PortalPermission;
import com.visualization.model.api.PortalUserPermission;
import com.visualization.model.db.SystemPermission;

import java.util.List;

public interface PermissionService {

    void createPermission(SystemPermission permission);

    void grantPermissionToUser(PortalUserPermission portalUserPermission);

    List<PortalPermission> getPermissionList(PortalPermission permission);

    List<GrantView> getGrantViewList(GrantView grantView);

    void retractPermission(GrantView grantView);
}
