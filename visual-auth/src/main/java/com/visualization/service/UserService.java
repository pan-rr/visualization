package com.visualization.service;

import com.visualization.enums.RoleEnum;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemUser;

import java.util.List;

public interface UserService  {

    void createUser(SystemUser user);

    SystemUser getNonNullUser(String oa);

    UserInfo login(SystemUser user);

    List<String> getPermission(Long userId);

    RoleEnum getRole(Long userId);
}
