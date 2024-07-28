package com.visualisation.filter;

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
public class CSVFilter implements ViewFilter {

    private final List<Consumer<Map<String, Object>>> chain = new LinkedList<>();

    @PostConstruct
    void init() {
        chain.add(CSVFilter::handlerId);
        chain.add(CSVFilter::destroyFileAfterFinish);
    }

    @Override
    public void filter(Map<String, Object> conf) {
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
            v = FileManager.getFileHandlerId();
            String filePath = String.valueOf(conf.get("filePath"));
            try {
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
