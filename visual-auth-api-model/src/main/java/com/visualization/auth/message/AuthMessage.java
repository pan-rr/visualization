package com.visualization.auth.message;

import java.util.Objects;

public class AuthMessage implements Comparable<AuthMessage> {

    public Integer messageType;

    public Long messageTimestamp;

    public String token;
    public Long timeout;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthMessage)) return false;
        AuthMessage that = (AuthMessage) o;
        return Objects.equals(messageType, that.messageType) && Objects.equals(messageTimestamp, that.messageTimestamp) && Objects.equals(token, that.token) && Objects.equals(timeout, that.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageType, messageTimestamp, token, timeout);
    }

    @Override
    public int compareTo(AuthMessage o) {
        return Long.compare(this.messageTimestamp, o.messageTimestamp);
    }

    @Override
    public String toString() {
        return "AuthMessage{" +
                "messageType=" + messageType +
                ", messageTimestamp=" + messageTimestamp +
                ", token='" + token + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
