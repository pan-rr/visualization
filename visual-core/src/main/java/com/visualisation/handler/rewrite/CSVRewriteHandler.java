package com.visualisation.handler.rewrite;

import com.visualisation.manager.FileManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class CSVRewriteHandler implements ViewRewriteHandler {

    private final List<Consumer<Map<String, Object>>> chain = new LinkedList<>();

    @PostConstruct
    void init() {
        chain.add(CSVRewriteHandler::handlerId);
        chain.add(CSVRewriteHandler::destroyFileAfterFinish);
    }

    @Override
    public void handle(Map<String, Object> conf) {
        if (!"csv".equals(String.valueOf(conf.get("viewType")))) return;
        for (Consumer<Map<String, Object>> consumer : chain) {
            consumer.accept(conf);
        }
    }

    /**
     * 空时使用默认值，非空使用指定的
     *
     * @param conf view conf
     */
    private static void handlerId(Map<String, Object> conf) {
        conf.compute("fileHandlerId", (k, v) -> {
            if (v != null) return v;
            v = FileManager.getDefaultFileHandlerId();
            String filePath = String.valueOf(conf.get("filePath"));
            try {
                if (filePath.startsWith("classpath:"))return "localFileHandler";
                String protocol = new URL(filePath).getProtocol();
                if ("http".equals(protocol)) {
                    return "httpFileHandler";
                }
            } catch (MalformedURLException e) {
                // 默认值
            }
            return v;
        });

    }

    private static void destroyFileAfterFinish(Map<String, Object> conf) {
        conf.compute("destroyFileAfterFinish", (k, v) -> v == null ? false : v);
    }
}
