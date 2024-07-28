package com.visualisation.manager;

import com.visualisation.handler.SpringApplicationHandler;
import com.visualisation.handler.file.FileHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        ApplicationContext ctx = SpringApplicationHandler.getCtx();
        FileHandler fileHandler = ctx.getBean(id, FileHandler.class);
        return fileHandler.download(sourcePath, param);
    }

    public static void outputFile(File file, String targetPath, String fileHandlerId, Map<?, ?> param) {
        String id = StringUtils.hasText(fileHandlerId) ? fileHandlerId : DEFAULT_HANDLER_ID.get();
        ApplicationContext ctx = SpringApplicationHandler.getCtx();
        FileHandler fileHandler = ctx.getBean(id, FileHandler.class);
        fileHandler.upload(file, targetPath, param);
    }

    @Deprecated
    public static File getFileByPath(String path) throws IOException {
        File file;
        if (path.startsWith("/")) {
            file = new FileSystemResource(path).getFile();
        } else if (path.startsWith("classpath:")) {
            file = new ClassPathResource(path.replace("classpath:", "")).getFile();
        } else {
            try {
                URL url = new URL(path);
                url.getProtocol();
                file = new FileUrlResource(url).getFile();
            } catch (Exception e) {
                file = new ClassPathResource(path).getFile();
            }
        }
        return file;
    }


}
