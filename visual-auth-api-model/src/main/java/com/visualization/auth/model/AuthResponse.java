package com.visualization.auth.model;

import java.io.Serializable;

public class AuthResponse implements Serializable {

    private static final long serialVersionUID = -2653296335402054926L;
    private String token;

    private Long timeout;

    private boolean tokenLegal;

    private boolean permissionLegal;

    private boolean roleLegal;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public boolean isTokenLegal() {
        return tokenLegal;
    }

    public void setTokenLegal(boolean tokenLegal) {
        this.tokenLegal = tokenLegal;
    }

    public boolean isPermissionLegal() {
        return permissionLegal;
    }

    public void setPermissionLegal(boolean permissionLegal) {
        this.permissionLegal = permissionLegal;
    }

    public boolean isRoleLegal() {
        return roleLegal;
    }

    public void setRoleLegal(boolean roleLegal) {
        this.roleLegal = roleLegal;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", timeout=" + timeout +
                ", tokenLegal=" + tokenLegal +
                ", permissionLegal=" + permissionLegal +
                ", roleLegal=" + roleLegal +
                '}';
    }
}
