package com.visualization.service;

import com.visualization.mapper.AuthGraphMapper;
import com.visualization.model.g6.G6;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AuthGraphService {

    @Resource
    private AuthGraphMapper authGraphMapper;

    public G6 getGraph(String pre, int level) {
        List<Map<String, Object>> nodes = authGraphMapper.getNodes(pre, level);
        return G6.getInstance(pre, nodes);
    }


}
