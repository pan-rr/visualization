package com.visualisation.handler;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileHandler {

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
