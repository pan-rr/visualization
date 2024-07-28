package com.visualisation.handler.file;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component(value = "localFileHandler")
public class LocalFileHandler implements FileHandler {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public File download(String sourcePath, Map<?, ?> uploadParam) {
        File file;
        try {
            if (sourcePath.startsWith("/")) {
                file = new FileSystemResource(sourcePath).getFile();
            } else if (sourcePath.startsWith("file://")) {
                file = new FileUrlResource(sourcePath).getFile();
            } else {
                file = new ClassPathResource(sourcePath.replace("classpath:", "")).getFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Override
    public void upload(File file, String targetPath, Map<?, ?> uploadParam) {
        Path target = Paths.get(targetPath);
        Path filePath = file.toPath();
        try {
            Files.move(filePath, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
