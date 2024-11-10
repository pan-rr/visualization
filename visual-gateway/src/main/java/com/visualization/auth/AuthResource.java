package com.visualization.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResource {
    private String pattern;
    private String resource;

    public static AuthResource replaceUnknownResource(AuthResource resource) {
        String s = "null";
        return Objects.isNull(resource) ? new AuthResource(s, s) : resource;
    }
}
