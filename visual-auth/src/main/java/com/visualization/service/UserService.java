package com.visualization.service;

import com.visualization.model.api.AuthResource;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;

import java.util.List;

public interface UserService  {

    void createUser(SystemUser user);

    void createSubTenant(PortalTenantUser tenantUser);

    SystemUser getNonNullUser(String oa);

    UserInfo login(SystemUser user);

    List<String> getPermission(Long userId);

    List<String> getRole(Long userId);

    AuthResource getUserTenantPermission(String tenantId);
}
