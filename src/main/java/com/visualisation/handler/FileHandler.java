package com.visualisation.handler;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    public static File getFileByPath(String path) throws IOException {
        File file;
        if (path.startsWith("/")) {
            Path p = Paths.get(path);
            file = p.toFile();

        } else {
            file = new ClassPathResource(path).getFile();
        }
        return file;
    }


}
