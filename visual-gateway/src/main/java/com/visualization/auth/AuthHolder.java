package com.visualization.auth;

import com.visualization.auth.message.AuthMessage;

import java.util.Objects;

public class AuthHolder {

    private String token;

    private Long timeout;

    private Long modifyTime;


    public AuthHolder() {
    }

    public void renewModifyTime() {
        this.modifyTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthHolder)) return false;
        AuthHolder that = (AuthHolder) o;
        return Objects.equals(token, that.token) && Objects.equals(timeout, that.timeout) && Objects.equals(modifyTime, that.modifyTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, timeout, modifyTime);
    }

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

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "AuthHolder{" +
                "token='" + token + '\'' +
                ", timeout=" + timeout +
                ", modifyTime=" + modifyTime +
                '}';
    }

    public static AuthHolder build(AuthMessage message) {
        AuthHolder holder = new AuthHolder();
        holder.timeout = message.timeout;
        holder.renewModifyTime();
        holder.token = message.token;
        return holder;
    }
}
