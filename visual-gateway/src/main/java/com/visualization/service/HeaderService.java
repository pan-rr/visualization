package com.visualization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HeaderService {

    @Value("${visual.auth.token.name}")
    private String tokenName;

    public Map<String, List<String>> buildAuthHeader(String authToken) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(tokenName, Collections.singletonList(authToken));
        return headers;
    }

    public Map<String, List<String>> buildAuthHeader(List<String> authToken) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(tokenName, authToken);
        return headers;
    }
}
