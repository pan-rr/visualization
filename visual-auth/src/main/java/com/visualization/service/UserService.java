package com.visualization.service;

import com.visualization.model.api.AuthResource;
import com.visualization.model.api.ChangePassword;
import com.visualization.model.api.PortalTenantUser;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;

public interface UserService  {

    boolean createUser(SystemUser user);

    void changePassword(ChangePassword request);

    void createSubTenant(PortalTenantUser tenantUser);

    SystemUser getNonNullUser(String oa);

    UserInfo login(SystemUser user);

    AuthResource getUserTenantPermission(String tenantId);

    void logout();
}
