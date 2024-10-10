package com.visualization.auth.model;

import java.io.Serializable;
import java.util.List;

public class AuthRequest implements Serializable {

    private String token;

    // second
    private Long timeout;

    private String role;

    private String permission;

    private boolean needLatest;

    private List<String> callbackURLs;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isNeedLatest() {
        return needLatest;
    }

    public void setNeedLatest(boolean needLatest) {
        this.needLatest = needLatest;
    }

    public List<String> getCallbackURLs() {
        return callbackURLs;
    }

    public void setCallbackURLs(List<String> callbackURLs) {
        this.callbackURLs = callbackURLs;
    }
}
