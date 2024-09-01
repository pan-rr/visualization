package com.visualization.handler.file;

import com.visualization.manager.FileManager;
import kotlin.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FileHandlerPriorityHandler {

    @Resource
    private Map<String, FileHandler> map;
    @PostConstruct
    void init() {
        List<Pair<String, FileHandler>> list = new ArrayList<>(map.size());
        map.forEach((k, v) -> list.add(new Pair<>(k, v)));
        list.sort((a, b) -> b.getSecond().getPriority() - a.getSecond().getPriority());
        FileManager.setFileHandlerId(list.get(0).getFirst());
    }
}
