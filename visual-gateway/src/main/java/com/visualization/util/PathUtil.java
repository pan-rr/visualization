package com.visualization.util;

import org.springframework.http.server.RequestPath;
import org.springframework.util.AntPathMatcher;

import java.util.List;

public class PathUtil {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    public static boolean match(String pattern, String path) {
        return matcher.match(pattern, path);
    }

    public static boolean anyMatch(List<String> patterns, RequestPath requestPath) {
        String value = requestPath.value();
        for (String pattern : patterns) {
            if (matcher.match(pattern, value)) {
                return true;
            }
        }
        return false;
    }

}
