package com.visualization.manager;

import com.visualization.handler.SpringContextHandler;
import com.visualization.handler.file.FileHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FileManager {

    private static final AtomicReference<String> DEFAULT_HANDLER_ID = new AtomicReference<>("localFileHandler");


    public static String getDefaultFileHandlerId() {
        return DEFAULT_HANDLER_ID.get();
    }

    public static void setFileHandlerId(String id) {
         DEFAULT_HANDLER_ID.set(id);
    }

    public static File getFile(String sourcePath, String fileHandlerId, Map<?, ?> param) {
        String id = StringUtils.hasText(fileHandlerId) ? fileHandlerId : DEFAULT_HANDLER_ID.get();
        ApplicationContext ctx = SpringContextHandler.getCtx();
        FileHandler fileHandler = ctx.getBean(id, FileHandler.class);
        return fileHandler.download(sourcePath, param);
    }

    public static void outputFile(File file, String targetPath, String fileHandlerId, Map<?, ?> param) {
        String id = StringUtils.hasText(fileHandlerId) ? fileHandlerId : DEFAULT_HANDLER_ID.get();
        ApplicationContext ctx = SpringContextHandler.getCtx();
        FileHandler fileHandler = ctx.getBean(id, FileHandler.class);
        fileHandler.upload(file, targetPath, param);
    }


}
