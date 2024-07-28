package com.visualisation.handler.file;

import com.visualisation.handler.SpringApplicationHandler;
import com.visualisation.manager.FileManager;
import kotlin.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FileHandlerPriorityHandler {
    @PostConstruct
    void init() {
        ApplicationContext ctx = SpringApplicationHandler.getCtx();
        Map<String, FileHandler> map = ctx.getBeansOfType(FileHandler.class);
        List<Pair<String, FileHandler>> list = new ArrayList<>(map.size());
        map.forEach((k, v) -> list.add(new Pair<>(k, v)));
        list.sort((a, b) -> b.getSecond().getPriority() - a.getSecond().getPriority());
        FileManager.setFileHandlerId(list.get(0).getFirst());
    }
}
