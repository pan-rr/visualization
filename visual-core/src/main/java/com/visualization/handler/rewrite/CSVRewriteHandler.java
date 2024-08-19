package com.visualization.handler.rewrite;

import com.visualization.constant.ViewConstant;
import com.visualization.constant.ViewTypeConstant;
import com.visualization.manager.FileManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        if (!ViewTypeConstant.CSV.equals(String.valueOf(conf.get(ViewConstant.VIEW_TYPE)))) return;
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
        conf.compute(ViewConstant.FILE_HANDLER_ID, (k, v) -> {
            if (v != null) return v;
            v = FileManager.getDefaultFileHandlerId();
            String filePath = String.valueOf(conf.get(ViewConstant.FILE_PATH));
//            try {
            if (filePath.startsWith("classpath:")) return "localFileHandler";
            if (filePath.startsWith("http")) return "httpFileHandler";
//                String protocol = new URL(filePath).getProtocol();
//                if ("http".equals(protocol)) {
//                    return "httpFileHandler";
//                }
//            } catch (MalformedURLException e) {
//                // 默认值
//            }
            return v;
        });

    }

    private static void destroyFileAfterFinish(Map<String, Object> conf) {
        conf.compute("destroyFileAfterFinish", (k, v) -> v == null ? false : v);
    }
}
