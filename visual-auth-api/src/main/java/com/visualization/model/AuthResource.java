package com.visualization.model;

import java.io.Serializable;
import java.util.List;

public class AuthResource implements Serializable {

    private String authKey;

    private Boolean permitAll;

    private List<String> resourceList;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public Boolean getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(Boolean permitAll) {
        this.permitAll = permitAll;
    }

    public List<String> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<String> resourceList) {
        this.resourceList = resourceList;
    }
}
