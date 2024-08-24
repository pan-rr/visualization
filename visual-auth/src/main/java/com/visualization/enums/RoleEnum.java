package com.visualization.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    NORMAL("普通角色"),
    ADMIN("管理员");

    private final String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }
}
